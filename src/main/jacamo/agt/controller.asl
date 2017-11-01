+sanction_application(
    id(SAId),
    time(SATime),
    decision_id(SDId),
    executor(Me)
  )
  <-
  ?sanction_decision(
    id(SDId),
    _,
    _,
    _,
    _,
    norm(
      id(NormId),_,_,_,target(NormTarget),_,_,_
    ),
    _,
    _
  );
  NormToMonitorWithoutAnnot = norm(
    id(NormId),_,_,_,target(NormTarget),_,_,_
  );
  NormToMonitorWithAnnot = norm(
    id(NormId),_,_,_,target(NormTarget),_,_,_
  )[H|T];
  ?current_round(ApplicationRound);
  .wait({+NormToMonitorWithoutAnnot}); // activation
  .wait({+NormToMonitorWithAnnot}); // violation/compliance
  cartago.invoke_obj("java.lang.System",currentTimeMillis,SOTime);
  ?current_round(CurrentRound);
  ?max_rounds_until_outcome(MaxWait);
  SO = sanction_outcome(
    id(_),
    time(SOTime),
    application_id(SAId),
    controller(Me),
    efficacy(Efficacy)
  );
  if ( (ApplicationRound + MaxWait) <= CurrentRound ) {
    Efficacy = indeterminate;
  } else {
    // TODO: prepare for deactivation as well
    if ( .member(violation_time(_),[H|T]) ) {
      Efficacy = effective;
    } else {
      Efficacy = ineffective;
    }
  }
  addOutcome(SO).
