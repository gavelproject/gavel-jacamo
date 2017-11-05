+!execute(SanctionDecision)
  : sanction_decision(
      id(SDId),
      time(Time),
      detector(Detector),
      evaluator(Me),
      target(Target),
      Norm,
      Sanction,
      cause(Cause)
    ) = SanctionDecision &
    sanction(
      id(SId),
      status(enabled),
      activation(Activation),
      Category,
      content(SContent)
    ) = Sanction
  <-
  !SContent;
  cartago.invoke_obj("java.lang.System",currentTimeMillis,SATime);
  .my_name(Me);
  SA = sanction_application(
    id(_),
    time(SATime),
    decision_id(SDId),
    executor(Me)
  );
  addApplication(SA).
