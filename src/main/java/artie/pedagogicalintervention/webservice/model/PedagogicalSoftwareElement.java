package artie.pedagogicalintervention.webservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedagogicalSoftwareElement {

    private String id;
    private String name;
    private List<PedagogicalSoftwareBlock> blocks = new ArrayList<>();


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

    /**
     * Function to add a new block
     * @param block
     */
    public void addBlock(PedagogicalSoftwareBlock block){this.blocks.add(block);}

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for(PedagogicalSoftwareBlock block: blocks){
            stringBuilder.append(block.toString());
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
