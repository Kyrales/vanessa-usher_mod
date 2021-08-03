import org.silverbulleters.usher.config.JobConfiguration
import org.silverbulleters.usher.config.stage.BddOptional
import org.silverbulleters.usher.config.stage.PrepareBaseOptional
import org.silverbulleters.usher.config.additional.InfoBase
import org.silverbulleters.usher.config.stage.SmokeOptional
import org.silverbulleters.usher.config.stage.SyntaxCheckOptional
import org.silverbulleters.usher.config.stage.TddOptional

def initDevFromSource(JobConfiguration config, PrepareBaseOptional optional) {
  def command = [
      "vrunner",
      "init-dev",
      "%credentionalID%",
      "--settings", config.getVrunnerConfig(),
      "--ibconnection", infobaseHelper.getConnectionString(config),
      "--src=${optional.getSourcePath()}",
      "--v8version", config.getV8Version(),
      "--nocacheuse"
  ]
  return command.join(" ")
}

def initDevWithTemplate(JobConfiguration config, PrepareBaseOptional optional) {
  // TODO: проверить существование `optional.getTemplate()`
  def command = [
      "vrunner",
      "init-dev",
      "%credentionalID%",
      "--settings", config.getVrunnerConfig(),
      "--ibconnection", infobaseHelper.getConnectionString(config),
      "--dt", optional.getTemplate(),
      "--v8version", config.getV8Version(),
      "--nocacheuse"
  ]
  return command.join(" ")
}

def compile(JobConfiguration config, PrepareBaseOptional optional) {
  def command = [
      "vrunner",
      "compile",
      "%credentionalID%",
      "--settings", config.getVrunnerConfig(),
      "--ibconnection", infobaseHelper.getConnectionString(config),
      "--src=${optional.getSourcePath()}",
      "-c",
      "--noupdate",
      "--v8version", config.getV8Version(),
      "--nocacheuse"
  ]
  return command.join(" ")
}

def loadRepo(JobConfiguration config, PrepareBaseOptional optional) {
  def command = [
      "vrunner",
      "loadrepo",
      "%credentionalID%",
      "--settings", config.getVrunnerConfig(),
      "--ibconnection", infobaseHelper.getConnectionString(config),
      "%credentionalStorageID%",
      "--v8version", config.getV8Version(),
      "--storage-name", optional.getRepo().getPath(),
      "--nocacheuse"
  ]
  return command.join(" ")
}

def updateDB(JobConfiguration config, PrepareBaseOptional optional) {
  def command = [
      "vrunner",
      "updatedb",
      "%credentionalID%",
      "--settings", config.getVrunnerConfig(),
      "--ibconnection", infobaseHelper.getConnectionString(config),
      "--v8version", config.getV8Version(),
      "--nocacheuse"
  ]
  return command.join(" ")
}

def migrate(JobConfiguration config, PrepareBaseOptional optional) {
  def command = [
      "vrunner",
      "run",
      "%credentionalID%",
      "--ibconnection", infobaseHelper.getConnectionString(config),
      "--settings", config.getVrunnerConfig(),
      "--command", "'ЗапуститьОбновлениеИнформационнойБазы;ЗавершитьРаботуСистемы;'",
      "--execute", '$runnerRoot/epf/ЗакрытьПредприятие.epf'
  ]
  return command.join(" ")
}

def syntaxCheck(JobConfiguration config, SyntaxCheckOptional optional) {
  def command = [
      "vrunner",
      "syntax-check",
      "%credentionalID%",
      "--settings", config.getVrunnerConfig(),
      "--allure-results2", optional.getAllurePath(),
      "--junitpath", "./out/junit/syntaxCheck.xml",
      "--ibconnection", infobaseHelper.getConnectionString(config),
      "--v8version", config.getV8Version()
  ]
  return command.join(" ")
}

def smoke(JobConfiguration config, SmokeOptional optional) {
  def pathToAllure = "${optional.getAllurePath()}/allure-smoke.xml"
  def command = [
      "vrunner",
      "xunit",
      optional.getTestPath(),
      "%credentionalID%",
      "--settings", config.getVrunnerConfig(),
      "--ibconnection", infobaseHelper.getConnectionString(config),
      "--v8version", config.getV8Version(),
      "--testclient", "%credentionalTestClientID%",
      "--reportsxunit", "ГенераторОтчетаJUnitXML{out/junit/smoke.xml};ГенераторОтчетаAllureXMLВерсия2{${pathToAllure}}",
      "--xddExitCodePath", "./xddExitCodePath.txt",
      "--xddConfig", optional.getXddConfig()
  ]
  return command.join(" ")
}

def xunit(JobConfiguration config, TddOptional optional) {
  def pathToAllure = "${optional.getAllurePath()}/allure-xdd.xml"
  def command = [
      "vrunner",
      "xunit",
      optional.getTestPath(),
      "%credentionalID%",
      "--settings", config.getVrunnerConfig(),
      "--ibconnection", infobaseHelper.getConnectionString(config),
      "--v8version", config.getV8Version(),
      "--testclient", "%credentionalTestClientID%",
      "--reportsxunit", "ГенераторОтчетаJUnitXML{out/junit/xdd.xml};ГенераторОтчетаAllureXMLВерсия2{${pathToAllure}}",
      "--xddExitCodePath", "./xddExitCodePath.txt",
      "--xddConfig", optional.getXddConfig()
  ]
  return command.join(" ")
}

def vanessa(JobConfiguration config, BddOptional optional) {
  def command = [
      "vrunner",
      "vanessa",
      "%credentionalID%",
      "--settings", config.getVrunnerConfig(),
      "--ibconnection", infobaseHelper.getConnectionString(config),
      "--v8version", config.getV8Version()
  ]
  return command.join(" ")
}