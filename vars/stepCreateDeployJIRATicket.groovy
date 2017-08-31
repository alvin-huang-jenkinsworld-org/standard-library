/*
* This step takes Jenkinsfile parameters and creates a JIRA deploy ticket when an artifact is promoted.
*/

/* if we have multiple jira tickets, we can loop through each of them */
def call (ArrayList<LinkedHashMap> multipleTickets) {
  for (i = 0; i < multipleTickets.size(); i++) {
    call(multipleTickets[i])
  }
}

def call(LinkedHashMap <String, String> jira_ticket) {
  /* set a default subject if not specified
   *
   * ex: "Deploy portal Release 1.0.0": where repo is 'portal' and tag is '1.0.0'
   */
  if (jira_ticket['summary'] == null) {
    jira_ticket['summary'] = "Deploy ${env.GIT_REPO_NAME} Release ${env.GIT_TAG}"
  }

  node {
    def issue = [
                  fields: [
                            project: [key: jira_ticket['project']],
                            /* toString is needed here because the interpolation creates a
                             * class org.codehaus.groovy.runtime.GStringImpl object while the JSON
                             * is looking for a java.lang.String object
                             */
                            summary: jira_ticket['summary'].toString(),
                            description: jira_ticket['description'].stripIndent(),
                            components: [[name: jira_ticket['component']]],
                            issuetype: [name: 'Story'] // we should always use the 'Deploy issue type
                          ]
                ]
    // assignees are optional
    if (jira_ticket['assignee'] != null) {
      issue['fields']['assignee'] = [name: jira_ticket['assignee']]
    }
    response = jiraNewIssue issue: issue, site: "jira-production"
    echo "Deploy ticket was created at https://alvinhuang.atlassian.net/browse/${response.data['key'].toString()}"
  }
}
