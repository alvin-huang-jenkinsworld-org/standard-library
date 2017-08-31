/*
* This step publishes HTML reports according to the html_pattern parameter.
*
* It accepts one LinkedHashMap of key value pairs for the publishHTML
* Jenkins pipeline step OR an ArrayList of LinkedHashMaps
*/

/* 
* If an ArrayList of LinkedHashMaps are passed in, we loop through them and 
* publish the HTML reports for each.
*/

def call (ArrayList<LinkedHashMap> multipleReports) {
  for (i = 0; i < multipleReports.size(); i++) {
    call(multipleReports[i])
  }
}

/*
* On each set of LinkedHashMap HTML parameters, we attempt to publish the
* HTML pages. 
*
* There is currently a workaround in the case that no directory is passed in,
* presumably because the HTML files are in the current directory. This is 
* because the publishHTML() step will upload the whole directory specified
* which can be large for a large workspace.
*
* Defaults:
* - allowMissing: false
* - alwaysLinkToLastBuild: false
* - keepAll: false
*/

def call(LinkedHashMap <String, String> report) {
  default_config = [
    allowMissing: false,
    alwaysLinkToLastBuild: false,
    keepAll: false
  ]

  LinkedHashMap <String, String> html_params = default_config + report
  // HACK: MDOPS-6494
  if (html_params["reportDir"] == null || html_params["reportDir"] == '.') {
    html_params["reportDir"] = "${html_params["reportName"]}"
    try {
      echo "[stdlib/stepPublishHTMLReport] Directory argument not given " +
           "for `stepPublishHTMLReport` method. '${html_params["reportName"]}' report! " +
           "Attempting workaround..."

      sh """mkdir -p "${html_params["reportName"]}" ||:
            cp -fv "${html_params["reportFiles"]}" "${html_params["reportName"]}"
            exit 0
            """
    } catch(Exception ex) {
      echo "[stdlib/stepPublishHTMLReport] Failed work around for MDOPS-6494 " +
           "Report ${html_params["reportName"]} cannot be uploaded"
      return
    }
  }
  
  publishHTML(target:html_params)
}
