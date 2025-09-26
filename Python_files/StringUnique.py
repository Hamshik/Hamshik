rawStr: str = input("Enter the letters: ")
def solution(rawStr:str) -> int:
    i:int = 0;
    counter:int = 0
    rawList: list[str] = []
    lists: list[str] = []
    countList:list[int] = []
    rawList = [char for char in rawStr]
    rawAns: str =  ""
    for chars in range(len(rawList)):
        for char in range(len(rawList)):
            rawAns  = rawList[char] + rawList[chars]
            if rawAns in lists:
                countList.append(len(rawAns))  
                rawAns = ""
                lists.clear()
            else:
                lists.append(rawList[char])
    countList.append(len(lists))
    counter = max(countList)
    return counter
print(solution(rawStr))
