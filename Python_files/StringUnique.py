rawStr: str = input("Enter the letters: ")
def solution(rawStr:str) -> int:
    i:int = 0;
    counter:int = 0
    rawList: list[str] = []
    lists: list[str] = []
    countList:list[int] = []
    rawList = [char for char in rawStr]
    for chars in rawList:
        if chars in lists:
            i += 1
            countList.append(len(lists))
            lists.clear()
            lists.append(chars)
        else:
            lists.append(chars)
        print(f"lists: {lists}", end="\n")
    countList.append(len(lists))
    counter = max(countList)
    return counter
print(solution(rawStr))