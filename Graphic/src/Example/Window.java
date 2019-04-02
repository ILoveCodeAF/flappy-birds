package Example;

import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
//import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public class Window {
    private long window;
    private int windowWidth;
    private int windowHeight;

    public Window(){
        this.windowWidth = 960;
        this.windowHeight = 462;
    }

    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        System.out.println("windowWidth = "+this.windowWidth);
        System.out.println("windowHeight = "+this.windowHeight);

        try{
            this.init();
            this.loop();

            // Free the window callbacks and destroy the window
            Callbacks.glfwFreeCallbacks(window);
            GLFW.glfwDestroyWindow(window);
        }
        finally {
            this.dispose();
        }


    }
    private void init(){
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if(!GLFW.glfwInit()){
            throw new IllegalStateException("Unable to intialize the GLFW window");
        }

        //Configure GLFW
        GLFW.glfwDefaultWindowHints();// optional, the current window hints are already the default
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);// the window will stay hidden after creation
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // the window will be resizable

        //create window
        this.window = GLFW.glfwCreateWindow(this.windowWidth, this.windowHeight, "window", MemoryUtil.NULL, MemoryUtil.NULL);
        if(this.window == MemoryUtil.NULL){
            throw new RuntimeException("Fail to create Window!!");
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        GLFW.glfwSetKeyCallback(this.window, new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE){
                    GLFW.glfwSetWindowShouldClose(window, true);// We will detect this in the rendering loo
                }
            }
        });

        // Get the thread stack and push a new frame
        try( MemoryStack stack = MemoryStack.stackPush() ){
            IntBuffer pWidth = stack.mallocInt(1);//int*
            IntBuffer pHeight = stack.mallocInt(1);//int*

            // Get the window size passed to glfwCreateWindow
            GLFW.glfwGetWindowSize(this.window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

            //center the window
            GLFW.glfwSetWindowPos(
                    this.window,
                    (vidmode.width()-pWidth.get())/2,
                    (vidmode.height()-pHeight.get())/2
            );//stack frame is popped automatically

            // Make the OpenGL context current
            GLFW.glfwMakeContextCurrent(this.window);

            // Enable v-sync
            GLFW.glfwSwapInterval(1);

            // Make the window visible
            GLFW.glfwShowWindow(window);
        }
    }
    private void loop(){
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        //set the clear color
        GL11.glClearColor(1f, 1f, 1f, 0f);
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

//        Renderer renderer = new Renderer();
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
        TextureState temp = new TextureState();
        temp.enter();
        while(!GLFW.glfwWindowShouldClose(this.window)){
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer


            temp.render(0f);
//            renderer.update(0.01f);
//            renderer.render(0f);



            GLFW.glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            GLFW.glfwPollEvents();
        }
        temp.exit();
//        renderer.dispose();
    }

    private void dispose(){
        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        new Window().run();
    }
}
