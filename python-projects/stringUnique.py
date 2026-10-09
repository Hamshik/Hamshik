def solution(Str: str) -> str:
    counter: int = 0
    lists: list[str] = []
    countList: list[int] = []

    for d in Str:
        if d not in lists:
            lists.append(d)
        else:
            if lists[0] == d:
                lists.pop(0)
                lists.append(d)
            else:
                countList.append(len(lists))
                lists.clear()
                lists.append(d)
        print(lists)

    countList.append(len(lists))
    counter = max(countList)
    return f"The length of longest unique letter group is {counter}"


while True:
    rawStr: str = input("Enter the letters: ").strip()
    if " " in rawStr and  rawStr.isalpha():
        break
    elif not("" == rawStr or rawStr.isalpha()):
        print("Invalid input. Please enter only letters.")
    else:
        break

if rawStr:
    print(solution(rawStr))
