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

import static jason.asSyntax.ASSyntax.createAtom;
import static jason.asSyntax.ASSyntax.parseStructure;

import java.util.Set;

import cartago.Artifact;
import cartago.LINK;
import cartago.OPERATION;
import cartago.ObsProperty;
import cartago.OpFeedbackParam;
import gavel.api.norm.Norm;
import gavel.api.nslink.NsLink;
import gavel.api.sanction.Sanction;
import gavel.impl.common.DefaultStatus;
import gavel.impl.norm.Norms;
import gavel.impl.nslink.NsLinks;
import gavel.impl.repo.DeJures;
import jason.asSyntax.Structure;
import jason.asSyntax.parser.ParseException;

/**
 * This artifact stores a regulative specification, which is composed of norms, sanctions, and
 * norm-sanction links. It provides operations to consult and make changes, as well as observable
 * properties which reflect the current state of the regulative specification.
 * 
 * @author igorcadelima
 *
 */
public final class DeJure extends Artifact {
  private gavel.api.repo.DeJure deJure;

  /**
   * Initializes {@link DeJure} repository based on data from the given regulative specification.
   * 
   * @param regulativeSpec path to file with the regulative specification
   */
  public void init(String regulativeSpec) {
    deJure = DeJures.fromSpecFile(regulativeSpec);
    deJure.getNorms().forEach(this::defineObsProperty);
    deJure.getSanctions().forEach(this::defineObsProperty);
    deJure.getNsLinks().forEach(this::defineObsProperty);
  }

  /** Handy method to define observable properties of an {@code obj}. */
  private void defineObsProperty(Object obj) {
    try {
      Structure s = parseStructure(obj.toString());
      defineObsProperty(s.getFunctor(), s.getTerms()
                                         .toArray());
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Handy method to update observable properties of an {@code obj}. */
  private void updateObsProperty(Object obj, String id) {
    try {
      Structure s = parseStructure(obj.toString());
      ObsProperty property = getObsPropertyByTemplate(s.getFunctor(), createAtom(id), null);
      property.updateValue(s.getTerms()
                            .toArray());
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Handy method to remove observable properties of a {@code obj}. */
  private void removeObsProperty(Object obj) {
    try {
      Structure s = parseStructure(obj.toString());
      removeObsPropertyByTemplate(s.getFunctor(), s.getTerms()
                                                   .toArray());
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /** Returns norms through {@code out}. */
  @LINK
  @OPERATION
  public void getNorms(OpFeedbackParam<Set<Norm>> out) {
    out.set(deJure.getNorms());
  }

  /**
   * Adds new norm and defines observable property for it.
   * 
   * Only instances of {@link Norm} or {@link String} should be passed as argument. If {@code norm}
   * is an instance of {@link String}, then it will be parsed using {@link Norms#tryParse(String)}
   * before being added to the repository.
   * 
   * @param norm norm to be added
   */
  @LINK
  @OPERATION
  public void addNorm(Object norm) {
    if (norm instanceof Norm)
      addNorm((Norm) norm);
    else if (norm instanceof String)
      addNorm(Norms.parse((String) norm));
    else
      failed("Expected " + String.class.getCanonicalName() + " or " + Norm.class.getCanonicalName()
          + " but got " + norm.getClass()
                              .getCanonicalName());
  }

  /**
   * Adds new norm and defines observable property for it.
   * 
   * Note that only norms with different ids from the ones present in the repository can be added,
   * even if disabled. If an already existing norm is tried to be added, such addition attempt is
   * just ignored.
   * 
   * @param norm norm to be added
   */
  private void addNorm(Norm norm) {
    if (deJure.addNorm(norm)) {
      defineObsProperty(norm);
    }
  }

  /**
   * Removes norm along with its observable property.
   * 
   * @param norm norm to be removed
   */
  @LINK
  @OPERATION
  public void removeNorm(String id) {
    // TODO: check whether operator agent is a legislator
    Norm norm = deJure.removeNorm(id);
    if (norm != null) {
      removeObsPropertyByTemplate(NsLinks.getStructureName(), null, createAtom(id), null);
      removeObsProperty(norm);
    }
  }

  /** Enables an existing norm with the given {@code id}. */
  @LINK
  @OPERATION
  public void enableNorm(String id) {
    Norm norm = deJure.getNorm(id);
    if (deJure.enableNorm(id)) {
      updateObsProperty(norm, id);
    }
  }

  /** Disables an existing norm with the given {@code id}. */
  @LINK
  @OPERATION
  public void disableNorm(String id) {
    Norm norm = deJure.getNorm(id);
    if (deJure.disableNorm(id)) {
      updateObsProperty(norm, id);
    }
  }

  /** Returns sanctions through {@code out}. */
  @LINK
  @OPERATION
  public void getSanctions(OpFeedbackParam<Set<Sanction>> out) {
    out.set(deJure.getSanctions());
  }

  /**
   * Adds new sanction and defines observable property for it.
   * 
   * Note that only sanctions with different ids from the ones present in the repository can be
   * added, even if disabled. If an already existing sanction is tried to be added, such addition
   * attempt is just ignored.
   * 
   * @param sanction sanction to be added
   */
  @LINK
  @OPERATION
  public void addSanction(Object sanction) {
    // TODO: check whether operator agent is a legislator
    if (sanction instanceof Sanction)
      addSanction((Sanction) sanction);
    else if (sanction instanceof String)
      addSanction(gavel.impl.sanction.Sanctions.tryParse((String) sanction));
    else
      failed("Expected " + String.class.getCanonicalName() + " or "
          + Sanction.class.getCanonicalName() + " but got " + sanction.getClass()
                                                                      .getCanonicalName());
  }

  /**
   * Adds new sanction and defines observable property for it.
   * 
   * Note that only sanction with different ids from the ones present in the repository can be
   * added, even if disabled. If an already existing sanction is tried to be added, such addition
   * attempt is just ignored.
   * 
   * @param sanction sanction to be added
   */
  private void addSanction(Sanction sanction) {
    if (deJure.addSanction(sanction)) {
      defineObsProperty(sanction);
    }
  }

  /**
   * Removes sanction along with observable property.
   * 
   * @param sanction sanction to be removed
   * @return {@code true} if sanction was removed successfully
   */
  @LINK
  @OPERATION
  public void removeSanction(String id) {
    // TODO: check whether operator agent is a legislator
    Sanction sanction = deJure.removeSanction(id);
    if (sanction != null) {
      removeObsPropertyByTemplate(NsLinks.getStructureName(), null, null, createAtom(id));
      removeObsProperty(sanction);
    }
  }

  /** Enables an existing sanction with the given {@code id}. */
  @LINK
  @OPERATION
  public void enableSanction(String id) {
    Sanction sanction = deJure.getSanction(id);
    if (deJure.enableSanction(id)) {
      updateObsProperty(sanction, id);
    }
  }

  /** Disables an existing sanction with the given {@code id}. */
  @LINK
  @OPERATION
  public void disableSanction(String id) {
    Sanction sanction = deJure.getSanction(id);
    if (deJure.disableSanction(id)) {
      updateObsProperty(sanction, id);
    }
  }

  /**
   * Adds new norm-sanction link and defines observable property for it.
   * 
   * @param nsLink link to be added
   */
  @LINK
  @OPERATION
  public void addNsLink(String nsLink) {
    // TODO: check whether operator agent is a legislator
    NsLink link = NsLinks.tryParse(nsLink);
    if (deJure.addNsLink(link)) {
      defineObsProperty(link.toString());
    }
  }

  /**
   * Removes existing link between norm and sanction with given ids.
   * 
   * @param normId id of the norm
   * @param sanctionId id of the sanction
   */
  @LINK
  @OPERATION
  public void removeLink(String normId, String sanctionId) {
    // TODO: check whether operator agent is a legislator
    NsLink link = deJure.removeNsLink(normId, sanctionId);
    if (link != null) {
      removeObsPropertyByTemplate(NsLinks.getStructureName(), null, createAtom(normId),
          createAtom(sanctionId));
    }
  }

  /** Enables an existing link between a norm and a sanction. */
  @LINK
  @OPERATION
  public void enableNsLink(String normId, String sanctionId) {
    if (deJure.enableNsLink(normId, sanctionId)) {
      ObsProperty property = getObsPropertyByTemplate(NsLinks.getStructureName(), null,
          createAtom(normId), createAtom(sanctionId));
      property.updateValues(createAtom(DefaultStatus.ENABLED.toString()), createAtom(normId),
          createAtom(sanctionId));
    }
  }

  /** Disables an existing link between a norm and a sanction. */
  @LINK
  @OPERATION
  public void disableNsLink(String normId, String sanctionId) {
    if (deJure.disableNsLink(normId, sanctionId)) {
      ObsProperty property = getObsPropertyByTemplate(NsLinks.getStructureName(), null,
          createAtom(normId), createAtom(sanctionId));
      property.updateValues(createAtom(DefaultStatus.DISABLED.toString()), createAtom(normId),
          createAtom(sanctionId));
    }
  }

}
