package javax.servlet;

import java.io.IOException;
import java.io.InputStream;

public abstract class ServletInputStream extends InputStream {
	public int readLine(byte[] b, int off, int len) throws IOException {
		if (len <= 0) {
			return 0;
		}
		int count = 0;
		int c;
		while ((c = read()) != -1) {
			b[(off++)] = ((byte) c);
			count++;
			if (c != 10)
				if (count == len) {
					break;
				}
		}
		return count > 0 ? count : -1;
	}
}
