package application;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

class MainTest {

	@Test
	void testMain() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		System.out.println("main");
		boolean isOutput = false;
		String[] args = null;
		String user = System.getProperty("user.home");
		final InputStream original = System.in;
		final FileInputStream fips = new FileInputStream(new File(user + "\\OneDrive\\Desktop\\TheRaven.txt"));
		System.setIn(fips);
		Main.main(args);
		System.setIn(original);
		if (Main.output.length() > 91)
		{
			isOutput = true;
		}
		else
		{
			isOutput = false;
		}
		assertEquals(isOutput, true, "Output is not null.");
	}

}
