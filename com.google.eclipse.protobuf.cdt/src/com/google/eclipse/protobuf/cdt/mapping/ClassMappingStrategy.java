/*
 * Copyright (c) 2012 Google Inc.
 *
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.google.eclipse.protobuf.cdt.mapping;

import static com.google.eclipse.protobuf.protobuf.ProtobufPackage.Literals.MESSAGE;

import org.eclipse.cdt.core.dom.IName;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.internal.core.dom.parser.cpp.*;
import org.eclipse.xtext.naming.QualifiedName;

import com.google.inject.Singleton;

/**
 * @author alruiz@google.com (Alex Ruiz)
 */
@SuppressWarnings("restriction")
@Singleton class ClassMappingStrategy implements IBindingMappingStrategy<CPPClassType> {

  @Override public CppToProtobufMapping createMappingFrom(IBinding binding) {
    CPPClassType classType = typeOfSupportedBinding().cast(binding);
    if (isMessage(classType)) {
      String[] segments = classType.getQualifiedName();
      QualifiedName qualifiedName = QualifiedName.create(segments);
      return new CppToProtobufMapping(qualifiedName, MESSAGE);
    }
    return null;
  }

  private boolean isMessage(CPPClassType classType) {
    ICPPBase[] bases = classType.getBases();
    if (bases.length != 1) {
      return false;
    }
    IName name = bases[0].getBaseClassSpecifierName();
    if (!(name instanceof CPPASTQualifiedName)) {
      return false;
    }
    CPPASTQualifiedName qualifiedName = (CPPASTQualifiedName) name;
    if (!qualifiedName.isFullyQualified()) {
      return false;
    }
    String qualifiedNameAsText = qualifiedName.toString();
    return "::google::protobuf::Message".equals(qualifiedNameAsText);
  }

  @Override public Class<CPPClassType> typeOfSupportedBinding() {
    return CPPClassType.class;
  }
}
