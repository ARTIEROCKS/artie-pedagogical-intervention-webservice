package artie.pedagogicalintervention.webservice.batch;

import artie.common.web.dto.SolutionDistance;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareData;
import artie.pedagogicalintervention.webservice.model.PedagogicalSoftwareSolution;
import artie.pedagogicalintervention.webservice.service.DistanceCalculationService;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareService;
import artie.pedagogicalintervention.webservice.service.PedagogicalSoftwareSolutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchDistanceService {
    private static final Logger log = LoggerFactory.getLogger(BatchDistanceService.class);
    @Autowired
    private PedagogicalSoftwareService softwareService;
    @Autowired
    private DistanceCalculationService distanceCalculationService;
    @Autowired
    private PedagogicalSoftwareSolutionService solutionService;

    public void process(){

        List<PedagogicalSoftwareData> elements = softwareService.findAll();
        log.info("Found " +elements.size() + " elements");

        for(PedagogicalSoftwareData psd: elements){
            log.info("Starting the process of Pedagogical Software Data id " + psd.getId());

            //We get all the possible solutions in the database for this exercise
            log.trace("Getting all the possible solutions for the exercise id (" + psd.getExercise().getId() +") and the student id (" + psd.getStudent().getId() + ")");
            List<PedagogicalSoftwareSolution> solutions = solutionService.findByExerciseAndUserId(psd.getExercise(), psd.getStudent().getUserId());

            //Calculates ARTIE distances between the pedagogical software data and the different solutions
            log.info("Calculating ARTIE distances. Solutions found: " + solutions.size() + " for exercise id (" + psd.getExercise().getId() +") and the student id (" + psd.getStudent().getId() + ")");
            PedagogicalSoftwareSolution bestSolution = null;
            SolutionDistance bestDistance = null;
            SolutionDistance currentDistance;
            for (PedagogicalSoftwareSolution solution: solutions){
                currentDistance = distanceCalculationService.distanceCalculation(psd, solution);
                if (bestDistance == null || bestDistance.getTotalDistance() > currentDistance.getTotalDistance()) {
                    bestDistance = currentDistance;
                    bestSolution = solution;
                }
            }

            if(bestDistance != null) {
                log.trace("Old ARTIE Distance: " + psd.getSolutionDistance().getTotalDistance() + " - New ARTIE Distance: " + bestDistance.getTotalDistance());
            }else{
                log.error("ARTIE Distance is NULL");
            }

            //Calculates the APTED distances with respect the best solution
            log.info("Calculating APTED distances. Solutions found: " + solutions.size() + " for exercise id (" + psd.getExercise().getId() +") and the student id (" + psd.getStudent().getId() + ")");
            SolutionDistance maximumDistance = null;
            double maximumTreeDistance = 0.0;
            double aptedDistance = 0.0;
            String tree  = psd.toString();
            String solutionTree = "";
            if (bestSolution != null) {
                maximumDistance = distanceCalculationService.distanceCalculation(new PedagogicalSoftwareData(), bestSolution);
                solutionTree = bestSolution.toString();
                maximumTreeDistance = distanceCalculationService.aptedDistanceCalculation("{}", solutionTree);
                aptedDistance = distanceCalculationService.aptedDistanceCalculation(tree, solutionTree);
            }
            log.info("Old APTED Distance: " + psd.getAptedDistance() + " - New APTED Distance: " + aptedDistance);

            //Calculates the grades
            double artieGrade = 0.0;
            double aptedGrade = 0.0;
            if(maximumDistance != null && bestDistance != null) {
                artieGrade = softwareService.calculateGrade(maximumDistance.getTotalDistance(), bestDistance.getTotalDistance(), 10);
                log.info("Old ARTIE Grade: " + psd.getGrade() + " - New ARTIE Grade: " + artieGrade);
            }
            aptedGrade = softwareService.calculateGrade(maximumTreeDistance, aptedDistance, 10);
            log.trace("Old Tree Grade: " + psd.getTreeGrade() + " - New Tree Grade: " + aptedGrade);

            //Sets all the information within the distance object
            psd.setRelatedSolution(bestSolution);
            if(maximumDistance != null) {
                psd.setMaximumDistance(maximumDistance.getTotalDistance());
            }
            if(bestDistance != null){
                psd.setSolutionDistance(bestDistance);
            }
            psd.setGrade(artieGrade);

            psd.setTree(tree);
            log.trace("Old Tree: " + psd.getTree() + " - New Tree: " + tree);
            psd.setSolutionTree(solutionTree);
            log.trace("Old Solution Tree: " + psd.getSolutionTree() + " - New Solution Tree: " + solutionTree);
            psd.setMaximumTreeDistance(maximumTreeDistance);
            psd.setAptedDistance(aptedDistance);
            psd.setTreeGrade(aptedGrade);

            //Transforms the pedagogical software data in the record
            log.info("Finished the process of Pedagogical Software Data id " + psd.getId());
        }

        log.info("Updating all the elements");
        softwareService.updateAll(elements);
        log.info("Updating process finished");
    }
}
