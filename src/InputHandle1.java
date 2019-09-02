import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
//import java.nio.IntBuffer;
import static org.lwjgl.glfw.GLFW.*;


public final class InputHandle1 {
    private long window;

    private final int KEYBOARD_SIZE = 512;
    private final int MOUSE_SIZE = 16;

    private int[] keyStates;
    private boolean[] activeKeys;

    private int[] mouseButtonStates;
    private boolean[] activeMouseButtons;
    private long lastMouseNS = 0;
    private long mouseDoubleClickPeriodNS; //5th of a second for double click.

    private int NO_STATE;

    private GLFWKeyCallback keyboard;
    private GLFWMouseButtonCallback mouse;
    private GLFWCursorPosCallback cursorPos;

    public InputHandle1(long window){
        this.window = window;


        this.keyStates = new int[KEYBOARD_SIZE];
        this.activeKeys = new boolean[KEYBOARD_SIZE];
        this.mouseButtonStates = new int[MOUSE_SIZE];
        this.activeMouseButtons = new boolean[MOUSE_SIZE];
        this.mouseDoubleClickPeriodNS = 1000000000 / 5;
        this.NO_STATE = -1;


        this.keyboard = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                activeKeys[key] = action != GLFW_RELEASE;
                keyStates[key] = action;
                if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE){
                    GLFW.glfwSetWindowShouldClose(window, true);// We will detect this in the rendering loop
                }
            }
        };
        glfwSetKeyCallback(this.window, this.keyboard);

        this.mouse = new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                activeMouseButtons[button] = action != GLFW_RELEASE;
                mouseButtonStates[button] = action;
            }
        };
        glfwSetMouseButtonCallback(this.window, this.mouse);

        this.cursorPos = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                /*nothing here*/
            }
        };
        glfwSetCursorPosCallback(this.window, this.cursorPos);
    }
    protected void update()
    {
        resetKeyboard();
        resetMouse();

//        glfwPollEvents();
//        Engine.getInput();
    }

    private void resetKeyboard()
    {
        for (int i = 0; i < keyStates.length; i++)
        {
            keyStates[i] = NO_STATE;
        }
    }

    private void resetMouse()
    {
        for (int i = 0; i < mouseButtonStates.length; i++)
        {
            mouseButtonStates[i] = NO_STATE;
        }

        long now = System.nanoTime();

        if (now - lastMouseNS > mouseDoubleClickPeriodNS)
            lastMouseNS = 0;
    }

    public boolean keyDown(int key)
    {
        return activeKeys[key];
    }

    public boolean keyPressed(int key)
    {
        return keyStates[key] == GLFW_PRESS;
    }

    public boolean keyReleased(int key)
    {
        return keyStates[key] == GLFW_RELEASE;
    }

    public boolean mouseButtonDown(int button)
    {
        return activeMouseButtons[button];
    }

    public boolean mouseButtonPressed(int button)
    {
        return mouseButtonStates[button] == GLFW_PRESS;
    }

    public boolean mouseButtonReleased(int button)
    {
        boolean flag = mouseButtonStates[button] == GLFW_RELEASE;

        if (flag)
            lastMouseNS = System.nanoTime();

        return flag;
    }

    public boolean mouseButtonDoubleClicked(int button)
    {
        long last = lastMouseNS;
        boolean flag = mouseButtonReleased(button);

        long now = System.nanoTime();

        if (flag && now - last < mouseDoubleClickPeriodNS)
        {
            lastMouseNS = 0;
            return true;
        }

        return false;
    }

    public double getXpos(){
        try(MemoryStack stack = MemoryStack.stackPush()){
            DoubleBuffer xpos = stack.mallocDouble(1);
            glfwGetCursorPos(this.window, xpos, null);
            return xpos.get();
        }
    }

    public double getYpos(){
        try(MemoryStack stack = MemoryStack.stackPush()){
            DoubleBuffer ypos = stack.mallocDouble(1);
            glfwGetCursorPos(this.window, null, ypos);
            return ypos.get();
        }
    }
}