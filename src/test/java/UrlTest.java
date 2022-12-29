package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlTest {

	public static void main(String[] args) {
		
		String regex = "^((http|https)://)?(www.)?([a-zA-Z0-9]+)\\.[a-z]+([a-zA-z0-9.?#]+)?";
		
		Pattern parenPattern = Pattern.compile(regex);

		Matcher matcher = parenPattern.matcher("http://local.gshare.co.kr:8181");
			        
		if(matcher.find()) {
			System.out.println(matcher.group(0));
		}
	}

}
