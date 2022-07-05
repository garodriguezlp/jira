///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.5.0
//DEPS com.fasterxml.jackson.core:jackson-databind:2.13.3

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.PropertiesDefaultProvider;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Callable;

import static com.fasterxml.jackson.core.JsonPointer.compile;

@Command(name = "jira", mixinStandardHelpOptions = true, version = "jira 0.1",
        description = "jira made with jbang",
        defaultValueProvider = PropertiesDefaultProvider.class)
class jira implements Callable<Integer> {

    @Option(names = "--token",
            required = true,
            paramLabel = "JIRA_TOKEN",
            defaultValue = "${JIRA_TOKEN}",
            description = "The jira token. Defaults to `token` property on '${sys:user.home}${sys:file.separator}.jira.properties'")
    private String token;

    @Option(names = "--host",
            required = true,
            paramLabel = "JIRA_HOST",
            defaultValue = "${JIRA_HOST}",
            description = "The jira host. Defaults to `host` property on '${sys:user.home}${sys:file.separator}.jira.properties'")
    private String host;

    public static void main(String... args) {
        int exitCode = new CommandLine(new jira()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://" + host + "/rest/api/latest/issue/WEL-8098"))
                .headers("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<InputStream> response = HttpClient.newBuilder().build()
                .send(request, HttpResponse.BodyHandlers.ofInputStream());

        JsonNode node = new ObjectMapper().readTree(response.body());

        System.out.println("Key: " + node.at(compile("/key")).asText());
        System.out.println("Summary: " + node.at(compile("/fields/summary")).asText());

        return 0;
    }
}
