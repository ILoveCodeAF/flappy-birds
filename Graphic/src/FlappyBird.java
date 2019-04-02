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
import java.util.Random;

public class FlappyBird {
    private long window;
    private int windowWidth;
    private int windowHeight;

    private InputHandle1 input;
    public FlappyBird(){
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
        this.window = GLFW.glfwCreateWindow(this.windowWidth, this.windowHeight, "Flappy Bird", MemoryUtil.NULL, MemoryUtil.NULL);
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

    public static class Pipe{
        private int pipeWidth;
        private int pipeHeight;
        public int height;
        public float posPipe;
        public int gapPipe;

        public Pipe(){
            pipeWidth = 0;
            pipeHeight = 0;
            height = 0;
            posPipe = 0;
            gapPipe = 0;
        }

        public Pipe(int pipeWidth, int pipeHeight, int posPipe, int gapPipe) {
            this.pipeWidth = pipeWidth;
            this.pipeHeight = pipeHeight;
            this.posPipe = posPipe;
            this.gapPipe = gapPipe;
        }

        public void setHeight(int x, int y){
            Random rand = new Random();
            this.height = rand.nextInt(y-x)+x;
        }

        public void update(int x, int y, float dpos, int cPosPipe, float scale){
            posPipe -= dpos;
            if(posPipe<=-pipeWidth*scale){
                posPipe = cPosPipe;
                setHeight(x, y);
            }
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


//        CharSequence t = "Hello!! Now I can write something T^T.\nHmm. It's late now.\nI need to sleep!!@@";
//


//        InputHandle1 inp = new InputHandle1();



        //background
        Texture2 background = new Texture2("imgs/background.png");
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int bgSpeed = 0;
        int count = 0;

        //foreground
        Texture2 foreground = new Texture2("imgs/fg.png");
        float fgSpeed = 0;
        float ds = 12f;//80 = 2step, aWid/4 = 120
        System.out.println("ds = "+ds);
//        //System.out.println(animate.getWidth());
//        boolean jump = false;
//        int x = 0, y = 0;

        //pipe
        Texture2 pipe = new Texture2("imgs/pipe.png");



        int nPipe = 4;
        int gapPipe = (1500-50*nPipe)/nPipe;
        int gap = 130;
        int pH, p;


        Pipe[] pipes = new Pipe[nPipe];

        int lastPosPipe = windowWidth;

        int i;
        for(i = 0; i<nPipe; ++i){
            pipes[i] = new Pipe(pipe.getWidth(), pipe.getHeight(), lastPosPipe, gap);
            pipes[i].setHeight(51, 450-gap);

            lastPosPipe += gapPipe;
        }

//        boolean play = false;

        Font score = new Font(30);
        int scoreCount = 0;

        Texture2[] flappybird = new Texture2[3];
        int fap = 0;
        flappybird[0] = new Texture2("imgs/flappybird/1.png");
        flappybird[1] = new Texture2("imgs/flappybird/2.png");
        flappybird[2] = new Texture2("imgs/flappybird/3.png");

        int bx = 300, by = 250;
        float bscale = 0.8f;
//        int x1 = 0, x2 = 0;
//        boolean fly = false;
//        int up = 3;
//        int down = 10;
        int countUp = 8;
        int[] arrayUp = new int[20];
        arrayUp[0] = 64-49;
        arrayUp[15] = -arrayUp[0];
        for(i = 6; i>=0; --i){
            arrayUp[7-i] = -i*i+(i+1)*(i+1);
            //System.out.println(arrayUp[8-i]);
            arrayUp[7-i] /= 2;
            arrayUp[8+i] = -arrayUp[7-i];
        }
        arrayUp[16] = arrayUp[15] = arrayUp[14];
        for(i = 17; i<20; ++i){
            arrayUp[i] = (i-9)*(i-9)-(i-8)*(i-8);
        }
//        int[] arrayDown = new int[6];
//        arrayDown[0] = -2;
//        for(i = 1; i<6; i++){
//            arrayDown[i] = -(i+1)*(i+1)+(i-1)*(i-1);
//        }
//        down = arrayUp[1];

        boolean stop = true;

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


            if(stop==false){
                //draw pipes
                if(by<=128/3){
                    stop = true;
                }
                pipe.begin();
                for(i = 0; i<nPipe; ++i){
                    if(pipes[i].posPipe <= windowWidth){
                        pH = pipes[i].pipeHeight-pipes[i].height*3;
                        p = (int)pipes[i].posPipe;
                        if(pipes[i].posPipe<=300+bscale*39 && pipes[i].posPipe+51>=300){
                            if(by<=128/3+pipes[i].height || by+bscale*39>=128/3+pipes[i].height+gap){
                                stop = true;
                            }
                        }
                        if(pipes[i].posPipe<300+bscale*39 && pipes[i].posPipe>=300+bscale*39-ds/3){
                            scoreCount++;
                        }
                        if(p<pipes[i].posPipe){
                            p++;
                        }
                        pipe.drawTextureRegion( p, 128/3, 0, pH, pipes[i].pipeWidth, pipes[i].height*3, (float)1/3, Color.WHITE);
                        pipe.drawTextureRegion( p, 128/3+pipes[i].height+gap, 0, 0, pipes[i].pipeWidth, pipes[i].pipeHeight-3*(pipes[i].height+gap), (float)1/3, Color.WHITE);
                    }
                    pipes[i].update(51, 450-gap, ds/3, lastPosPipe+gapPipe, (float)1/3);
                    lastPosPipe = (int)pipes[i].posPipe;
                }
                pipe.end();
                pipe.prepareAtrib();
                pipe.render();

                score.drawText(scoreCount+"", 480f,450f, Color.WHITE);

                by += arrayUp[countUp];
                if(countUp<15){
                    countUp++;
                }
                if(input.mouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)){
                    //fly = true;
                    countUp = 2;
                }
            }
            else{
                by = 250;
                if(input.keyPressed(GLFW.GLFW_KEY_SPACE)) {
                    stop = false;
                    scoreCount = 0;
                    countUp = 8;

                    lastPosPipe = windowWidth;
                    for(i = 0; i<nPipe; ++i){
                        pipes[i].pipeWidth = pipe.getWidth();
                        pipes[i].pipeHeight = pipe.getHeight();
                        pipes[i].posPipe = lastPosPipe;
                        pipes[i].gapPipe = gap;
                        pipes[i].setHeight(51, 450-gap);

                        lastPosPipe += gapPipe;
                    }

                }
            }

            if(fap<3){
                flappybird[1].begin();
                flappybird[1].drawTextureRegion(bx, by, 0, 0, flappybird[1].getWidth(), flappybird[1].getWidth(), bscale, Color.WHITE);
                flappybird[1].end();
                flappybird[1].prepareAtrib();
                flappybird[1].render();
            }
            else{
                flappybird[2].begin();
                flappybird[2].drawTextureRegion(bx, by, 0, 0, flappybird[2].getWidth(), flappybird[2].getWidth(), bscale, Color.WHITE);
                flappybird[2].end();
                flappybird[2].prepareAtrib();
                flappybird[2].render();
            }
            fap++;
            if(fap==6)
                fap = 0;

            GLFW.glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            GLFW.glfwPollEvents();

        }

//        texture.dispose();
        renderer.dispose();
        background.dispose();
        foreground.dispose();
        for(i = 0; i<nPipe; ++i){
            pipes[i] = null;
        }
        pipe.dispose();
        score.dispose();

        flappybird[0].dispose();
        flappybird[1].dispose();
        flappybird[2].dispose();
    }

    private void dispose(){
        // Terminate GLFW and free the error callback
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        new FlappyBird().run();
    }
}

