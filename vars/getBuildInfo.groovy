def call() {
  buildInfo = Artifactory.newBuildInfo()
  buildInfo.env.capture = true
  buildInfo.env.collect()

  return buildInfo
}
