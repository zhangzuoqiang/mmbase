package nl.vpro.redactie.config;


public class Param {
    String name;
    String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return "param: [name=" + name + ",value=" + value + "]";
    }
}
