package org.silverbulleters.usher.config.stage

class SmokeOptional extends BaseOptional {
    static final EMPTY = new SmokeOptional()

    String xddConfig = "./tools/JSON/smokeTestRunnerConf.json"
    String testPath = '$addroot/tests/smoke'
    String allurePath = "./out/smoke/allure"

    SmokeOptional() {
        name = "Smoke"
        timeout = 100
    }

}