package in.hamshik.Interpreter.shared;

import in.hamshik.Helpers.VarHelpers;



record Type(VarHelpers.Type type, Type innerType){}