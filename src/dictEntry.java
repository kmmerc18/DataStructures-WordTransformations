public class dictEntry {
    /**
     * Used in concurrence with Main.java for the Letterman project,
     * this allows the creation of a dictEntry object with associated String word
     * and dictEntry of the term morphed to become it, as well as a boolean marker "used"
     * to indicate it has been morphed in Main
     */
    private String word;
    private dictEntry prev;
    private Boolean used;

    // constructor
    public dictEntry(String term) {
        this.word = term;
        this.prev = null;
        this.used = false;
    }

    // instance methods
    public String getWord() {
        return this.word;
    }
    public dictEntry getPrev() {
        return this.prev;
    }
    public Boolean getUsed() {
        return this.used;
    }
    public void setPrev(dictEntry i) {
        this.prev = i;
    }
    public void setUsed() {
        this.used = true;
    }
}
