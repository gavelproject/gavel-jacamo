package org.gavelproject.jacamo;

import cartago.OpFeedbackParam;
import jason.JasonException;
import jason.asSemantics.Agent;
import jason.asSyntax.parser.ParseException;

/**
 * An enum that contains the types of agents which are supported.
 */
enum AgentType {
  JASON {
    Object[] getPlansOf(String file) {
      Agent agent = new Agent();
      try {
        agent.initAg();
        agent.parseAS(AgentType.class.getResourceAsStream(file));
      } catch (ParseException | JasonException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return agent.getPL()
                  .getPlans()
                  .toArray();
    }

    @Override
    void getPlansForDetector(OpFeedbackParam<Object> plans) {
      plans.set(getPlansOf("/agt/detector.asl"));
    }

    @Override
    void getPlansForEvaluator(OpFeedbackParam<Object> plans) {
      plans.set(getPlansOf("/agt/evaluator.asl"));
    }

    @Override
    void getPlansForExecutor(OpFeedbackParam<Object> plans) {
      // TODO Auto-generated method stub

    }

    @Override
    void getPlansForController(OpFeedbackParam<Object> plans) {
      // TODO Auto-generated method stub

    }

    @Override
    void getPlansForLegislator(OpFeedbackParam<Object> plans) {
      // TODO Auto-generated method stub

    }
  };

  /**
   * Get Jason plans for detectors.
   * 
   * @param plans output parameter used to return the plans
   */
  abstract void getPlansForDetector(OpFeedbackParam<Object> plans);

  /**
   * Get Jason plans for evaluator.
   * 
   * @param plans output parameter used to return the plans
   */
  abstract void getPlansForEvaluator(OpFeedbackParam<Object> plans);

  /**
   * Get Jason plans for executor.
   * 
   * @param plans output parameter used to return the plans
   */
  abstract void getPlansForExecutor(OpFeedbackParam<Object> plans);

  /**
   * Get Jason plans for controller.
   * 
   * @param plans output parameter used to return the plans
   */
  abstract void getPlansForController(OpFeedbackParam<Object> plans);

  /**
   * Get Jason plans for legislator.
   * 
   * @param plans output parameter used to return the plans
   */
  abstract void getPlansForLegislator(OpFeedbackParam<Object> plans);
}
