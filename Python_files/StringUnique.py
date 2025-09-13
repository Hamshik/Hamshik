rawStr: str = input("Enter the letters: ")
count:int = 0
def solution(rawStr:str):
    i:int = 0;
    rawList: list[str] = []
    lists: list[list[str]] = []
    unilist: list[str] = []
    countList:list[int] = []
    for ch in rawStr:
        rawList.append(ch)

    for ch in rawList:
        if ch not in unilist:
            unilist.append(ch)
        if ch in unilist:
            lists.append(unilist.copy())
    for countLetter in lists:
        i += 1
        if (i == len(lists)):
            counter = len(countLetter)
        else:
            continue
    return counter
print(solution(rawStr))