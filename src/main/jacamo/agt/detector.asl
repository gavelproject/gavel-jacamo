+!watch_normative_events
  <-
  NormTemplate = norm(id(Id),
				status(enabled),
				activation(Activation),
				issuer(Issuer),
				target(Target),
				deactivation(Deactivation),
				deadline(Deadline),
				content(Content))[H|T];
  .setof(NormTemplate,
    NormTemplate & not .member(activation_time(_), [H|T]),
    Norms
  );
  for ( .member(Norm,Norms) ) {
    !!watch_events_ruled_by_norm(Norm);
  }.


+!watch_events_ruled_by_norm(Norm)
  : norm(_,_,_,_,_,_,_,_)[H|T] = Norm
  <-
  NormTemplate = norm(id(Id),
    status(Status),
    activation(Activation),
	issuer(Issuer),
	target(Target),
	deactivation(Deactivation),
	deadline(Deadline),
	content(Content))[H|T];
  NormTemplate = Norm;
  while(true) {
  	// Get active norm instances
  	.setof(NormTemplate,
      NormTemplate[Head|Tail]
  	    & .member(activation_time(Time), [Head|Tail])
  	    & not (
  	      .member(deactivation_time(_), [Head|Tail])
  	      | .member(compliance_time(_), [Head|Tail])
  	      | .member(violation_time(_), [Head|Tail])
  	    ),
  	  ActiveNormInstances
  	);
  	.wait(Activation & not Deactivation);
  	// Get instances which are not already active
    .setof(
      NormTemplate,
      not .member(NormTemplate,ActiveNormInstances),
      NewNormInstances
    );
    cartago.invoke_obj("java.lang.System",currentTimeMillis,TimeNow);
    for ( .member(NewNormInstance, NewNormInstances) ) {
      .add_annot(NewNormInstance,activation_time(TimeNow), ActiveNormInstance);
      +ActiveNormInstance;
      !!monitor_norm_instance(ActiveNormInstance);
    }
  }.


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
    -+FinishedInstance;
  } else {
  	if (Aim) {
    .add_annot(Instance,compliance_time(Time),FinishedInstance);
    } else { // Deadline is true
      .add_annot(Instance,violation_time(Time),FinishedInstance);
    }
    .abolish(Instance);
    -+FinishedInstance;
    !report(FinishedInstance);
  }.


+!report(FinishedInstance)
  <-
  .print("To do: +!report(FinishedInstance)").