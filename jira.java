///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.5.0
//DEPS com.fasterxml.jackson.core:jackson-databind:2.13.3

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.PropertiesDefaultProvider;
import picocli.CommandLine.Spec;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.concurrent.Callable;

import static java.lang.System.out;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

@Command(name = "jira",
        mixinStandardHelpOptions = true,
        version = "jira 0.1",
        description = "jira made with jbang",
        defaultValueProvider = PropertiesDefaultProvider.class,
        subcommands = jira.Unresolved.class,
        header = {
                "     __.__               ",
                "    |__|__|___________   ",
                "    |  |  \\_  __ \\__  \\  ",
                "    |  |  ||  | \\// __ \\_",
                "/\\__|  |__||__|  (____  /",
                "\\______|              \\/ "
        })
class jira {

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

    @Spec
    private CommandSpec spec;

    public static void main(String... args) {
        int exitCode = new CommandLine(new jira()).execute(args);
        System.exit(exitCode);
    }

    public void printHeader() {
        Arrays.stream(spec.usageMessage().header()).forEach(out::println);
        out.println();
    }

    public String getToken() {
        return token;
    }

    public String getHost() {
        return host;
    }

    @Command(name = "unresolved",
            mixinStandardHelpOptions = true)
    static class Unresolved implements Callable<Integer> {

        @ParentCommand
        private jira parent;

        @Override
        public Integer call() throws Exception {
            parent.printHeader();
            HttpResponse<InputStream> response = callApiForCurrentUnresolvedIssues();
            if (response.statusCode() == HTTP_OK) {
                new ObjectMapper().readTree(response.body()).at("/issues")
                        .forEach(this::printIssue);
            }
            return 0;
        }

        private HttpResponse<InputStream> callApiForCurrentUnresolvedIssues() throws IOException, InterruptedException {
            String jqlEncoded = encode("assignee = currentUser() AND resolution is EMPTY", UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://" + parent.getHost() + "/rest/api/latest/search?jql=" + jqlEncoded))
                    .headers("Authorization", "Bearer " + parent.getToken())
                    .GET()
                    .build();

            return HttpClient.newBuilder().build()
                    .send(request, HttpResponse.BodyHandlers.ofInputStream());
        }

        private void printIssue(JsonNode issue) {
            String key = issue.at(("/key")).asText();
            String summary = issue.at("/fields/summary").asText();
            out.println("--- ----------------------------------------------------------------------------");
            out.println("[" + key + "]: " + summary);
            out.println("browse: https://" + parent.getHost() + "/browse/" + key);
        }
    }
}
