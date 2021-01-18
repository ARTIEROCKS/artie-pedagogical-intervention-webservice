package artie.pedagogicalintervention.webservice.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PedagogicalSoftwareElement {

    private String id;
    private String name;
    private List<PedagogicalSoftwareBlock> blocks = new ArrayList<>();

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<PedagogicalSoftwareBlock> getBlocks() {
        return blocks;
    }
    public void setBlocks(List<PedagogicalSoftwareBlock> blocks) {
        this.blocks = blocks;
    }

    /**
     * Default constructor
     */
    public PedagogicalSoftwareElement() {
    }

    /**
     * Parameterized constructor
     * @param id
     * @param name
     * @param blocks
     */
    public PedagogicalSoftwareElement(String id, String name, List<PedagogicalSoftwareBlock> blocks) {
        this.id = id;
        this.name = name;
        this.blocks = blocks;
    }


    /**
     * Overrides equals
     * @param obj
     * @return
     */
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        PedagogicalSoftwareElement objElement = (PedagogicalSoftwareElement) obj;

        //Checks if the id and the name are equals
        if(!this.id.equals(objElement.getId())) return false;
        if(!this.name.equals(objElement.getName())) return false;

        //Checks if all the blocks are equals
        boolean result = this.blocks.size() == objElement.getBlocks().size();
        for(PedagogicalSoftwareBlock b : this.blocks){
            result = result && (objElement.blocks.stream().filter(oe -> oe.equals(b)).count() > 0);
        }
        return result;
    }

    /**
     * Overrides clone
     * @return
     */
    public PedagogicalSoftwareElement clone(){
        List<PedagogicalSoftwareBlock> cloneBlocks = (this.blocks != null ? this.blocks.stream().map(b -> b.clone()).collect(Collectors.toList()) : null);
        return new PedagogicalSoftwareElement(this.id, this.name, cloneBlocks);
    }
}
