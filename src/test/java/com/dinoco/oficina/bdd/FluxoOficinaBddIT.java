package com.dinoco.oficina.bdd;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/ordem-servico-pagamento.feature")
@ConfigurationParameter(key = "cucumber.glue", value = "com.dinoco.oficina.bdd")
@ConfigurationParameter(key = "cucumber.plugin", value = "pretty,html:target/cucumber/relatorio.html,json:target/cucumber/relatorio.json,junit:target/cucumber/resultado.xml")
@ConfigurationParameter(key = "cucumber.execution.parallel.enabled", value = "false")
public class FluxoOficinaBddIT {
}
