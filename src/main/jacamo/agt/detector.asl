+!detect_normative_events
  <-
  NormTemplate = norm(id(Id),
    status(enabled),
    activation(Activation),
    issuer(Issuer),
    target(Target),
    deactivation(Deactivation),
    deadline(Deadline),
    content(Content))[H|T];
  // Get new norm instances
  .setof(NormTemplate,
    NormTemplate
      & not (.member(activation_time(Time), [H|T])
        & not (
          .member(deactivation_time(_), [H|T])
          | .member(compliance_time(_), [H|T])
          | .member(violation_time(_), [H|T])
        )
      )
      & Activation
      & not Deactivation,
    NewNormInstances
  );
  cartago.invoke_obj("java.lang.System",currentTimeMillis,Time);
  for ( .member(NormTemplate, NewNormInstances) ) {
    .add_annot(NormTemplate,activation_time(Time), Instance);
    +Instance;
    !monitor_norm_instance(Instance);
  }.


+!monitor_norm_instance(Instance)
  : norm(id(Id),
	status(enabled),
	activation(Activation),
	issuer(Issuer),
	target(Target),
	deactivation(Deactivation),
	deadline(Deadline),
	content(obligation(Aim))) = Instance
  <-
  .wait(Deactivation | Aim | Deadline);
  cartago.invoke_obj("java.lang.System",currentTimeMillis,Time);
  if (Deactivation) {
    .add_annot(Instance,deactivation_time(Time),FinishedInstance);
  } elif (Aim) {
    .add_annot(Instance,compliance_time(Time),FinishedInstance);
  } else { // Deadline is true
    .add_annot(Instance,violation_time(Time),FinishedInstance);
  }
  .abolish(Instance);
  -+FinishedInstance;
  !report(FinishedInstance).
  
+!report(FinishedInstance)
  <-
  .print("To do: +!report(FinishedInstance)").