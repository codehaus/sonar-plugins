package org.sonar.plugins.taglist.tests;

public class Hello {
    private String hello;

    public Hello(String s) {
        //TODO
        this.hello = s;
    }

    @Deprecated
    public String say() {
        return hello;
    }

    /**
     * @deprecated
     */
    public void deprecatedMethod() {

    }

    /**
     * @todo
     */
    public void todoMethod() {
   // SIMON
   //SIMON
    }

    protected int notCalled() {
        int i = 0;
        i++; // TODO
        return i + 3;
    }
}
