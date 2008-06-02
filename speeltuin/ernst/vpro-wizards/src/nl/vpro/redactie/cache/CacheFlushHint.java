package nl.vpro.redactie.cache;
import java.util.HashMap;
import java.util.Map;

public class CacheFlushHint {
    private Map<String,Object> properties = new HashMap<String, Object>();
    private int type;

    /**
     * For each request a cache flush hint of this type will be added. use it to do stuff
     * you always want to do like call your own cache flush logic that is not related to the different cache flush hint types.
     */
    public static final int TYPE_REQUEST = 0;
    /**
     * For each relation that was created or removed a hint of this type will be crated.
     * If one action couses multiple relation mutations (like first delete a relation node, and then create a new one)
     * only one hint is created.
     */
    public static final int TYPE_RELATION = 1;
    /**
     * for every node that is created, deleted or updated, a hint of this type is created.
     */
    public static final int TYPE_NODE = 2;

    private int relationNumber = -1;
    private int nodeNumber = -1;
    private int sourceNodeNumber = -1;
    private int destinationNodeNumber = -1;

    public int getDestinationNodeNumber() {
        return destinationNodeNumber;
    }

    public void setDestinationNodeNumber(int destinationNodeNumber) {
        this.destinationNodeNumber = destinationNodeNumber;
    }

    public int getNodeNumber() {
        return nodeNumber;
    }

    public void setNodeNumber(int nodeNumber) {
        this.nodeNumber = nodeNumber;
    }

    public int getRelationNumber() {
        return relationNumber;
    }

    public void setRelationNumber(int relationNumber) {
        this.relationNumber = relationNumber;
    }

    public int getSourceNodeNumber() {
        return sourceNodeNumber;
    }

    public void setSourceNodeNumber(int sourceNodeNumber) {
        this.sourceNodeNumber = sourceNodeNumber;
    }

    public CacheFlushHint(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }


    public Object getProperty(String name){
        return properties.get(name);
    }
    public void setProperty(String name, Object value){
        properties.put(name, value);
    }

    public String toString(){
        return "type: "+getTypeGUI()+", relation number: "+getRelationNumber()+", node number: "+getNodeNumber()+", source node number: "+getSourceNodeNumber()+", destination node number: "+getDestinationNodeNumber();
    }

    private String getTypeGUI(){
        switch (getType()) {
        case 0:
            return "request";

        case 1:
            return "relation";

        case 2:
            return "node";
        default:
            return "unsupported type: "+getType();
        }
    }

}
