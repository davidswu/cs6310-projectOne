package edu.gatech.cs6310.projectOne;


public class ProjectOne {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        String path = null;
        for (int i = 0; i < args.length; i++) {
            String flag = args[i];
            if (flag.startsWith("-")) {
                switch (flag.substring(1)) {
                    case "i":
                        path = args[i+1];
                        break;
                }
            }
        }

        float result = Scheduler.calculateSchedule(path);
        System.out.printf("X=%.2f", result);
	}

}
