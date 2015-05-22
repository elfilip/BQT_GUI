package qe.entity.result;

import javax.swing.JButton;

public class RefreshResults {
	private static JButton refreshButton;

	public static void setRefreshButton(JButton button) {
		refreshButton = button;
	}
	
	public static JButton getRefreshButton(){
		return refreshButton;
	}
}
