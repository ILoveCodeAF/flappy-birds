import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
//import java.nio.FloatBuffer;
//import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public final class InputHandle {
    private long window;

    private GLFWKeyCallback keyCallback;
    private GLFWMouseButtonCallback mouseButtonCallback;
    private GLFWCursorPosCallback cursorPosCallback;

    private Map<Integer, Integer> keyStateMap;//<key, state>
    private Map<Integer, Integer> mouseButtonStateMap;//<button, state>

//    private static class Key{
//        private int key;
//
//        public Key(int key) {
//            this.key = key;
//        }
//
//        public int getKey() {
//            return key;
//        }
//
//        public void setKey(int key) {
//            this.key = key;
//        }
//
//        @Override
//        public String toString() {
//            return this.getKey()+"";
//        }
//    }
//
////    private static class Action{
////        private int state;
////
////    }
//
//    private static class MouseButton {
//        private int button;
//
//        public MouseButton(int button) {
//            this.button = button;
//        }
//
//        public int getButton() {
//            return button;
//        }
//
//        public void setButton(int button) {
//            this.button = button;
//        }
//
//        @Override
//        public String toString() {
//            return this.getButton()+"";
//        }
//    }
//
//    public static class State{
//        private int state;
//
//        public State(int state) {
//            this.state = state;
//        }
//
//        public int getState() {
//            return state;
//        }
//
//        public void setState(int state) {
//            this.state = state;
//        }
//
//        @Override
//        public String toString() {
//            return this.getState()+"";
//        }
//    }

    public InputHandle(long window){
        this.window = window;

        this.keyStateMap = new HashMap<>();
        this.mouseButtonStateMap = new HashMap<>();

        this.keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                /*do something */
                keyStateMap.put(key, action);
                if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE){
                    GLFW.glfwSetWindowShouldClose(window, true);// We will detect this in the rendering loo
                }
            }
        };
        GLFW.glfwSetKeyCallback(this.window, this.keyCallback);

        this.mouseButtonCallback = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                /*do something*/
                mouseButtonStateMap.put(button, action);
            }
        };
        GLFW.glfwSetMouseButtonCallback(this.window, this.mouseButtonCallback);

        this.cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                /*do something*/
//                System.out.println("T^T");
            }
        };
        GLFW.glfwSetCursorPosCallback(this.window, this.cursorPosCallback);
    }

    public boolean keyPressed(int key){
        if(this.keyStateMap == null)
            return false;
        if(this.keyStateMap.get(key) == null)
            return false;
        return this.keyStateMap.get(key) == GLFW.GLFW_PRESS;
    }

    public boolean keyReleased(int key){
        if(this.keyStateMap == null)
            return true;
        if(this.keyStateMap.get(key) == null)
            return true;
        return this.keyStateMap.get(key) == GLFW.GLFW_RELEASE;
    }

    public boolean mouseButtonPressed(int button){
        if(this.mouseButtonStateMap == null)
            return false;
        if(this.mouseButtonStateMap.get(button) == null )
            return false;
        return this.mouseButtonStateMap.get(button) == GLFW.GLFW_PRESS;
    }

    public boolean mouseButtonReleased(int button){
        if(this.mouseButtonStateMap == null)
            return true;
        if(this.mouseButtonStateMap.get(button) == null )
            return true;
        return this.mouseButtonStateMap.get(button) == GLFW.GLFW_RELEASE;
    }

    public double getXpos(){
        try(MemoryStack stack = MemoryStack.stackPush()){
            DoubleBuffer xpos = stack.mallocDouble(1);
            GLFW.glfwGetCursorPos(this.window, xpos, null);
            return xpos.get();
        }
    }

    public double getYpos(){
        try(MemoryStack stack = MemoryStack.stackPush()){
            DoubleBuffer ypos = stack.mallocDouble(1);
            GLFW.glfwGetCursorPos(this.window, null, ypos);
            return ypos.get();
        }
    }

    @Override
    public String toString() {
        for(Map.Entry<Integer, Integer> entry : this.keyStateMap.entrySet()){
            System.out.println(entry.getKey()+" "+entry.getValue());
        }
        return "";
    }

    public static void main(String[] args) {
//        Map<Integer, Integer> map = new HashMap<>();
//        map.put(new Integer(2), new Integer(3));
//        map.put(new Integer(2), new Integer(6));
//        System.out.println(map.get(new Integer(2))+"    "+map.get(new Integer(4)));
    }
}