/* Jenkinsfile Entrypoint
 * - Opinionated Workflow Wrapper 
 */
class standardBuild extends baseBuild {
  def process(config) {
    node(config.machine) {
      try {
        stepBuildArtifacts(config)
      } catch (err) {
        // if the build script fails and there are html test results, we can publish them
        if(config.html_pattern) {
          stepPublishHTMLReport(config.html_pattern)
        }
        throw err
      }
      if(config.html_pattern) {
        stepPublishHTMLReport(config.html_pattern)
      }
      stepPublishArtifacts(config)
    }
  }
}
