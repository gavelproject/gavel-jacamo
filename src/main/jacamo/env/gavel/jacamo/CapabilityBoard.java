/*******************************************************************************
 * MIT License
 *
 * Copyright (c) Igor Conrado Alves de Lima <igorcadelima@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package gavel.jacamo;

import java.util.Arrays;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import gavel.impl.capability.DefaultCapability;
import gavel.impl.common.Enums;
import gavel.impl.repo.CapabilityBoards;
import jason.JasonException;
import jason.asSemantics.Agent;
import jason.asSyntax.parser.ParseException;

/**
 * Board where capabilities may be stored so that agents may acquire them through operations.
 * 
 * @author igorcadelima
 *
 */
public final class CapabilityBoard extends Artifact {
  private gavel.api.repo.CapabilityBoard cb;

  public void init() {
    cb = CapabilityBoards.of();
    setupProps();
  }

  public void init(String file) {
    cb = CapabilityBoards.fromFile(file);
    setupProps();
  }

  private void setupProps() {
    Object[] capabilities = Arrays.stream(cb.getCapabilities())
                                  .map(c -> c.toString())
                                  .toArray();
    defineObsProperty("capabilities", new Object[] {capabilities});
    defineObsProperty("detectors", new Object[] {cb.getDetectors()
                                                   .toArray()});
    defineObsProperty("evaluators", new Object[] {cb.getEvaluators()
                                                    .toArray()});
    defineObsProperty("executors", new Object[] {cb.getExecutors()
                                                   .toArray()});
    defineObsProperty("controllers", new Object[] {cb.getControllers()
                                                     .toArray()});
    defineObsProperty("legislators", new Object[] {cb.getLegislators()
                                                     .toArray()});
  }

  /**
   * Return default plans to perform actions which are supposed to be conducted by holders of a
   * certain {@code capability} depending on the {@code agentType}.
   * 
   * @param capability capability to be acquired
   * @param agentType type of the agent which should acquire the capability
   * @param plans output parameter used to return the plans
   */
  @OPERATION
  public void acquireCapability(String capability, OpFeedbackParam<Object> plans) {
    switch (Enums.lookup(DefaultCapability.class, capability)) {
      case DETECTOR:
        plans.set(getPlansFrom("/agt/detector.asl"));
        break;
      case EVALUATOR:
        plans.set(getPlansFrom("/agt/evaluator.asl"));
        break;
      case EXECUTOR:
        plans.set(getPlansFrom("/agt/executor.asl"));
        break;
      case CONTROLLER:
        plans.set(getPlansFrom("/agt/controller.asl"));
        break;
      case LEGISLATOR:
        plans.set(getPlansFrom("/agt/legislator.asl"));
        break;
    }
  }

  private Object[] getPlansFrom(String file) {
    Agent agent = new Agent();
    try {
      agent.initAg();
      agent.parseAS(CapabilityBoard.class.getResourceAsStream(file));
    } catch (ParseException | JasonException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return agent.getPL()
                .getPlans()
                .toArray();
  }

  @OPERATION
  public void registerAg(String ag, String capability) {
    switch (Enums.lookup(DefaultCapability.class, capability)) {
      case DETECTOR:
        cb.registerAg(ag, DefaultCapability.DETECTOR);
        updateObsProperty("detectors", new Object[] {cb.getDetectors()
                                                       .toArray()});
        break;
      case EVALUATOR:
        cb.registerAg(ag, DefaultCapability.EVALUATOR);
        updateObsProperty("evaluators", new Object[] {cb.getEvaluators()
                                                        .toArray()});
        break;
      case EXECUTOR:
        cb.registerAg(ag, DefaultCapability.EXECUTOR);
        updateObsProperty("executors", new Object[] {cb.getExecutors()
                                                       .toArray()});
        break;
      case CONTROLLER:
        cb.registerAg(ag, DefaultCapability.CONTROLLER);
        updateObsProperty("controllers", new Object[] {cb.getControllers()
                                                         .toArray()});
        break;
      case LEGISLATOR:
        cb.registerAg(ag, DefaultCapability.LEGISLATOR);
        updateObsProperty("legislators", new Object[] {cb.getLegislators()
                                                         .toArray()});
        break;
    }
  }

  @OPERATION
  public void registerSelfAs(String capability) {
    String agName = getCurrentOpAgentId().getAgentName();
    registerAg(agName, capability);
  }
}
