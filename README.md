# jira

> [jbang](https://www.jbang.dev/) powered ⚡ cli tool for [Atlassian's Jira](https://www.atlassian.com/software/jira).

```
     __.__
    |__|__|___________
    |  |  \_  __ \__  \
    |  |  ||  | \// __ \_
/\__|  |__||__|  (____  /
\______|              \/
```

## Want to give it a try?

-  Bash:

    ```
    curl -Ls https://sh.jbang.dev | bash -s - jira@garodriguezlp/jira --help
    ```

- Windows Powershell:

    ```
    iex "& { $(iwr -useb https://ps.jbang.dev) } jira@garodriguezlp/jira --help"
    ```

- Install as a Jbang app

    ```
    jbang app install jira@garodriguezlp/jira
    ```

## Configuration

Create a `.jira.properties` file on your `$HOME` directory to set the default value for the supported options.

```properties
host=<Jira Host>
token=<Jira HTTP API Bearer token>
project=<PROJECT KEY>
```

> Here's an example

```properties
host=jira.mycompany.com
token=ODE4NDM4ODY4ODM0OteGL9zBQ4+j+QizNH9bGByLKxT6
project=PROJ
```

## Why `jira` CLI?

```
I really like to play around with Jbang :D ¯\_(ツ)_/¯
```
