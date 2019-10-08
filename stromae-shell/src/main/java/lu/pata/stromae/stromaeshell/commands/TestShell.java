package lu.pata.stromae.stromaeshell.commands;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class TestShell {

    @Value("${app.url}")
    private String url;

    @ShellMethod("Test the concatenation of two strings")
    public String test(
            @ShellOption String text1,
            @ShellOption String text2
    ){
        return text1+"="+text2+" "+url;
    }
}
