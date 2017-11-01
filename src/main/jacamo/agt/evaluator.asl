+!sublist(_,0,N,[]): N<1. // N is a floating point number

+!sublist([_A|B],M,N,S)
  : M>0 & M<N
  <- !sublist(B,M-1,N-1,S).
  
+!sublist([A|B],M,N,S)
  : M==0 & M<N & N2=N-1
  <- 
  !sublist(B,0,N2,D);
  S=[A|D].


+!active_sanctions_for(NormInstance,Sanctions)
  : norm(id(Id),
      status(Status),
      activation(Activation),
      issuer(Issuer),
      target(Target),
      deactivation(Deactivation),
      deadline(Deadline),
      content(Content)) = NormInstance
  <-
  SanctionWithVar = sanction(
    id(SId),
    status(enabled),
    condition(Condition),
    Category,
    content(VarSContent)
  );
  Sanction = sanction(
    id(SId),
    status(enabled),
    condition(Condition),
    Category,
    content(SContent)
  );
  NsLink = nslink(status(enabled),nid(Id),sid(SId));
  .findall(
    Sanction,
    NsLink
      & SanctionWithVar
      & Condition
      & .term2string(VarSContent,StrVarSContent)
      & .term2string(SContent,StrVarSContent),
    Sanctions
  ).


+!decide_sanctions(NormInstance,DecidedSanctions)
  <-
  !active_sanctions_for(NormInstance,Options);

  // Shuffle list of sanction options
  .shuffle(Options,ShuffledOptions);

  // Get a random number of linked sanctions
  NumDecidedSanctions = math.random(.length(ShuffledOptions) + 1);
  !sublist(ShuffledOptions, 0, NumDecidedSanctions, DecidedSanctions).


+!evaluate(NormInstance)[source(Source)]
  <-
  Norm = norm(id(Id),
    status(enabled),
    activation(Activation),
    issuer(Issuer),
    target(Target),
    deactivation(Deactivation),
    deadline(Deadline),
    content(Content));
  Norm[H|T] = NormInstance;
  .my_name(Me);
  if (Source == self) {
    Detector = Me;
  } else {
    Detector = Source;
  }
  SD = sanction_decision(
    id(SDId),
    time(Time),
    detector(Detector),
    evaluator(Me),
    target(Target),
    norm(Norm),
    sanction(Sanction),
    cause(Cause)
  );
  if ( .member(violation_time(_),[H|T]) ) {
      Cause = violation;
  } else {
      Cause = compliance;
  }
  cartago.invoke_obj("java.lang.System",currentTimeMillis,Time);
  !decide_sanctions(NormInstance,DecidedSanctions);
  for ( .member(Sanction,DecidedSanctions) ) {
      addDecision(SD,SDId);
      !choose_executor(SD,Executor);
      .send(Executor, achieve, execute(SD));
  }.


+!choose_executor(SanctionDecision,Executor)
  : .my_name(Me) & executors(Executors) & .member(Me,Executors)
  <- Executor = Me.


+!choose_executor(SanctionDecision,Executor)
  <-
  ?executors(Executors);
  .shuffle(Executors,Shuffled);
  .nth(0,Shuffled,Executor).
