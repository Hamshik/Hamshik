def solution(rawStr:str) -> str:
    k:int = 0;
    counter:int = 0
    rawList: list[str] = []
    lists: list[str] = []
    countList:list[int] = []
    rawList = [char for char in rawStr]
    for d in rawList:
        k  += 1
        if (d not in lists):
            lists.append(d)
        else:
            if(lists[0] == d):
                lists.pop(0)
                rawList.pop(0)
                rawList.append(d)
            else:
                countList.append(len(lists))
                lists.clear()
                lists.append(d)
    if (rawList[len(rawList) - 1] not in lists):
        lists.append(rawList[len(rawList) - 1])
    countList.append(len(lists))
    countList.append(len(lists))
    counter = max(countList)
    return f"The longest unique letter  of group is {lists} and its length is {counter}"

while True:
    rawStr: str = input("Enter the letters: ")
    if not(rawStr == "" or rawStr.isalpha()):
        print("invaild input")
    else:
        break
print(solution(rawStr))
