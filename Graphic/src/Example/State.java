package Example;

public interface State {

    /**
     * Handles input of the state.
     */
    public void input();

    /**
     * Updates the state (fixed timestep).
     */
    public default void update() {

    }

    /**
     * Updates the state (variable timestep)
     *
     * @param delta Time difference in seconds
     */
    public void update(float delta);

    /**
     * Renders the state (no interpolation).
     */
    public default void render() {
        render(1f);
    }

    /**
     * Renders the state (with interpolation).
     *
     * @param alpha Alpha value, needed for interpolation
     */
    public void render(float alpha);

    /**
     * Gets executed when entering the state, useful for initialization.
     */
    public void enter();

    /**
     * Gets executed when leaving the state, useful for disposing.
     */
    public void exit();

}