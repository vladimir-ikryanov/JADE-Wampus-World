#!/bin/bash
javac -cp lib/jade.jar src/ua/nure/*.java src/ua/nure/agents/*.java
java -cp lib/jade.jar:src/.:. jade.Boot -agents world:ua.nure.agents.WumpusWorldAgent\;navigator:ua.nure.agents.NavigatorAgent\;speleologist:ua.nure.agents.SpeleologistAgent