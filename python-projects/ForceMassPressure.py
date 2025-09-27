# Calculation of force and pressure
x = str(input("Which Do you want to calculate force, mass,pressure or area: "));
xl = x.lower();
if (xl == "pressure"):
    n = int(input("Enter the force exertted: "));
    m = int(input("Enter the Area exertted by Force: "));
    p = n*m**-2;
    print("The formula of pressure is Pa = Nm**-2");
    print("The pressure is ",p,"Pa");
elif (xl == "force"):
    Pa = int(input("Enter the Pressure: "));
    a = int(input("Enter the Area: "));
    f = Pa*m**2;
    print("The Actacul formula is F = ma, but here we use f = pa*m**2 \n because the formula for Pa = Nm**-2, \n when we move the m**-2 to thr rhs it becomes pam**2");
    print("The force is ",f);
elif (xl == "area"):
    pa = int(input("Enter the Pressure: "));
    n = int(input("Enter the force exertted: "));
    a0 = n / pa;
    a = a0**1/2;
    print("the formula for area is a = (n/pa)**1/2");
    print("The area is", a);
elif (xl == "mass"):
    a = int(input("Enter the Area: "));
    f = int(input("Enter the force exertted: "));
    m = a/f;
    print("Mass is" , m);
else:
    print(x,"is not suppoorted by this programe");
    
