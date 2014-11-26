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

		fps = (int) (frameCounter / (deltaT / 1000));

		if (deltaT > displayFrequency)
			init();
	}

	private void init() {
		StartTime = System.currentTimeMillis();
		frameCounter = 0;

		System.out.println("FPS: " + fps);
	}
}
