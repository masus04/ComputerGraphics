package jrtr;

public class FpsCounter {

	double StartTime;
	long frameCounter;
	int fps;
	float displayFrequency;

	public FpsCounter(float displayFrequency) {
		this.displayFrequency = displayFrequency * 1000;
		init();
	}

	public void getFps() {
		double deltaT = System.currentTimeMillis() - StartTime;
		frameCounter++;

		if (deltaT > displayFrequency)
			init();

		fps = (int) (frameCounter / (deltaT / displayFrequency));
	}

	private void init() {
		StartTime = System.currentTimeMillis();
		frameCounter = 0;

		System.out.println("FPS: " + fps);
	}
}
