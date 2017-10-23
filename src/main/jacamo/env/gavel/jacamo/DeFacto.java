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

import static jason.asSyntax.ASSyntax.parseStructure;

import cartago.Artifact;
import cartago.LINK;
import cartago.OPERATION;
import gavel.api.sanction.SanctionApplication;
import gavel.api.sanction.SanctionDecision;
import gavel.api.sanction.SanctionOutcome;
import gavel.impl.repo.DeFactos;
import gavel.impl.sanction.SanctionApplications;
import gavel.impl.sanction.SanctionDecisions;
import gavel.impl.sanction.SanctionOutcomes;
import jason.asSyntax.Structure;
import jason.asSyntax.parser.ParseException;

/**
 * @author igorcadelima
 *
 */
public final class DeFacto extends Artifact {
  private gavel.api.repo.DeFacto deFacto = DeFactos.of();

  /**
   * Adds new sanction decision.
   * 
   * Only instances of {@link SanctionDecision} or {@link String} should be passed as argument. For
   * both cases, the decision will be added using {@link #addDecision(SanctionDecision)}. If
   * {@code sd} is an instance of {@link String}, then it will be parsed using
   * {@link SanctionDecisions#parse(String)} before being added to the repository.
   * 
   * @param sd sanction decision to be added
   */
  @LINK
  @OPERATION
  public void addDecision(Object sd) {
    if (sd instanceof SanctionDecision)
      addDecision((SanctionDecision) sd);
    else if (sd instanceof String)
      addDecision(SanctionDecisions.parse((String) sd));
    else
      failed("Expected " + String.class.getCanonicalName() + " or "
          + SanctionDecision.class.getCanonicalName() + " but got " + sd.getClass()
                                                                        .getCanonicalName());
  }

  /** Adds new sanction decision {@code sd}. */
  private void addDecision(SanctionDecision sd) {
    try {
      deFacto.addDecision(sd);
      Structure s = parseStructure(sd.toString());
      defineObsProperty(s.getFunctor(), s.getTerms()
                                         .toArray());
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Adds new sanction application.
   * 
   * Only instances of {@link SanctionApplication} or {@link String} should be passed as argument.
   * For both cases, the application will be added using
   * {@link #addApplication(SanctionApplication)}. If {@code sa} is an instance of {@link String},
   * then it will be parsed using {@link SanctionApplications#tryParse(String)} before being added
   * to the repository.
   * 
   * @param sd sanction application to be added
   */
  @LINK
  @OPERATION
  public void addApplication(Object sa) {
    if (sa instanceof SanctionApplication)
      addApplication((SanctionApplication) sa);
    else if (sa instanceof String)
      addApplication(SanctionApplications.tryParse((String) sa));
    else
      failed("Expected " + String.class.getCanonicalName() + " or "
          + SanctionApplication.class.getCanonicalName() + " but got " + sa.getClass());
  }

  /** Adds new sanction application {@code sa}. */
  private void addApplication(SanctionApplication sa) {
    try {
      deFacto.addApplication(sa);
      Structure s = parseStructure(sa.toString());
      defineObsProperty(s.getFunctor(), s.getTerms()
                                         .toArray());
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Adds new sanction outcome.
   * 
   * Only instances of {@link SanctionOutcome} or {@link String} should be passed as argument. For
   * both cases, the outcome will be added using {@link #addOutcome(SanctionOutcome)}. If {@code so}
   * is an instance of {@link String}, then it will be parsed using
   * {@link SanctionOutcomes#tryParse(String)} before being added to the repository.
   * 
   * @param so sanction outcome to be added
   */
  @LINK
  @OPERATION
  public void addOutcome(Object so) {
    if (so instanceof SanctionOutcome)
      addOutcome((SanctionOutcome) so);
    else if (so instanceof String)
      addOutcome(SanctionOutcomes.tryParse((String) so));
    else
      failed("Expected " + String.class.getCanonicalName() + " or "
          + SanctionOutcome.class.getCanonicalName() + " but got " + so.getClass());
  }

  /** Adds new sanction outcome {@code so}. */
  private void addOutcome(SanctionOutcome so) {
    try {
      deFacto.addOutcome(so);
      Structure s = parseStructure(so.toString());
      defineObsProperty(s.getFunctor(), s.getTerms()
                                         .toArray());
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
