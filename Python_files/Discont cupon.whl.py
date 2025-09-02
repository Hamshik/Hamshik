#Discount cupon
x = str(input("Enter the items name: "));
c = input("Enter the cupon Number: ");
x = x.lower();
if x == "tv" and c == "TV-2025-1234-HFYds":
    print("You got an 20% discount to", x);
elif x == "srew Driver" and c == "Screw-2025-Driver-3524-HRRT":
    print("You got 40% Discont to",x);
elif x == "pc" and c == "PC-2025-4738-FDSR":
    print("You Got 10% discount", x);
elif x == "backup ups" and c == "Backup-UPS-2025-HGGD":
    print("You got 90% discount", x);
else:
    print("please Check the Name or Cuppont No.");
exit(code=None)
