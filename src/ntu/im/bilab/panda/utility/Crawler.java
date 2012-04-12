package ntu.im.bilab.panda.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Crawler{

	private String url;
	public String HtmlContent;

	private String NewLine = System.getProperty("line.separator");

	private void init() {
		this.HtmlContent = null;
	}
	
	public void Start(String url) {
		init();
		this.url = url;
		this.StartCrawling();
	}
	
	private void StartCrawling() {
		StringBuffer buffer = new StringBuffer();

		try {

			URL NewsUrl = new URL(url);
			InputStreamReader isr = new InputStreamReader(NewsUrl.openStream());

			while (!isr.ready()) {
				// wait until the full web page has been downloaded.
			}

			BufferedReader br = new BufferedReader(isr);

			while (br.ready()) {
				buffer.append(br.readLine() + NewLine);
			}

			this.HtmlContent = buffer.toString();

			br.close();
			isr.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}