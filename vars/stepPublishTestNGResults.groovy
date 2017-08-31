def call(config){
  stage('Publishing TestNG Results'){
    step([$class: 'Publisher', reportFilenamePattern: "${config.testng_report_pattern}"])
  }
}
