import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
//import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

//import java.awt.*;
import java.awt.*;
import java.nio.IntBuffer;

public class RunAndJump {
    private long window;
    private int windowWidth;
    private int windowHeight;

    private InputHandle1 input;
    public RunAndJump(){
        this.windowWidth = 1000;
        this.windowHeight = 500;
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
        this.window = GLFW.glfwCreateWindow(this.windowWidth, this.windowHeight, "Run and Jump", MemoryUtil.NULL, MemoryUtil.NULL);
        if(this.window == MemoryUtil.NULL){
            throw new RuntimeException("Fail to create Window!!");
        }

//        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
//        GLFW.glfwSetKeyCallback(this.window, new GLFWKeyCallback() {
//            public void invoke(long window, int key, int scancode, int action, int mods) {
//                if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE){
//                    GLFW.glfwSetWindowShouldClose(window, true);// We will detect this in the rendering loo
//                }
//            }
//        });
        this.input = new InputHandle1(this.window);

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
        }
        // Make the OpenGL context current
        GLFW.glfwMakeContextCurrent(this.window);

        // Enable v-sync
        GLFW.glfwSwapInterval(1);

        // Make the window visible
        GLFW.glfwShowWindow(window);
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

        //render a triangle
        Renderer1 renderer = new Renderer1();
//        GL11.glEnable(GL11.GL_TEXTURE_2D);

//        //render a image
//        Texture1 texture = new Texture1("imgs/Ahri.png");
////        texture.prepareToRender();
////        //pipeline T^T
//        texture.begin();
//        texture.drawTextureRegion();
//        texture.end();
//
//        texture.prepareAtrib();

        Font text = new Font();
//        CharSequence t = "Hello!! Now I can write something T^T.\nHmm. It's late now.\nI need to sleep!!@@";
//
        CharSequence myText = "";

//        InputHandle1 inp = new InputHandle1();


        Texture2 animate = new Texture2("imgs/Animate.png");

        Texture2 background = new Texture2("imgs/background.png");
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int bgSpeed = 0;
        int count = 0;

        Texture2 foreground = new Texture2("imgs/fg.png");
        float fgSpeed = 0;
        float ds = (float)animate.getWidth()/320*3;//80 = 2step, aWid/4 = 120
        System.out.println("ds = "+ds);
        //System.out.println(animate.getWidth());
        boolean jump = false;
        int x = 0, y = 0;

        while(!GLFW.glfwWindowShouldClose(this.window)){
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // clear the framebuffer

//            texture.render();

//            text.drawText(t, this.windowWidth/2, this.windowHeight/2, Color.CYAN);


//            renderer.update(0.01f);
//            renderer.render(0f);

            background.begin();
            background.drawTextureRegion( 0, 0, bgSpeed, 0,bgWidth/2, bgHeight, (float)1/3, Color.WHITE);
            background.end();
            background.prepareAtrib();
            background.render();

            bgSpeed++;
            if(bgSpeed>=bgWidth/2){
                bgSpeed = 0;
            }

            foreground.begin();
            foreground.drawTextureRegion( 0, 0, (int)fgSpeed, 0,bgWidth/2, bgHeight, (float)1/3, Color.WHITE);
            foreground.end();
            foreground.prepareAtrib();
            foreground.render();

            fgSpeed += ds;
            if(fgSpeed>=bgWidth/2){
                fgSpeed = 0;
            }

            if(!jump){
                x = y = 128/3;
            }
            else{
                x = 128/3;
                y = 128/3 + (-(count-45)*(count-45)+225)/5;
                if(count == 60)
                    jump = false;
            }

            animate.begin();
            animate.drawTextureRegion(x, y, (count/10)*animate.getWidth()/8, animate.getHeight()/4, animate.getWidth()/8, animate.getHeight()/4, 1f, Color.WHITE);
            animate.end();
//        texture2.prepareToRender();
            animate.prepareAtrib();
            animate.render();

            count++;
            if(count==80){
                count = 0;
            }

//            text.drawText(t, this.windowWidth/2, this.windowHeight/2, Color.CYAN);
//            if(input.keyPressed(GLFW.GLFW_KEY_K)){
//                myText = "k was pressed. JustKidding";
//                System.out.println(myText);
//            }
//
//            text.drawText(myText, this.windowWidth/2, this.windowHeight/2, Color.CYAN);
//            renderer.update(0.01f);
//            renderer.render(0f);
//            text.drawFont();

//            System.out.println(inp.getXpos()+"  "+inp.getYpos());

            if(!jump && this.input.keyPressed(GLFW.GLFW_KEY_UP)){
                jump = true;
                count = 30;
            }

            GLFW.glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            GLFW.glfwPollEvents();
        }

//        texture.dispose();
        renderer.dispose();
        text.dispose();
        animate.dispose();
        background.dispose();
        foreground.dispose();
    }

    private void dispose(){
        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        new RunAndJump().run();
    }
}

