def get_key(lists_of_student_grades, TARGET):
    KEYS = []
    for i in range(len(lists_of_student_grades)):
        VALUE = lists_of_student_grades[i - 1][1]
        KEY = lists_of_student_grades[i - 1][0]
        if TARGET == VALUE:     KEYS.append(KEY)
        else:   continue
    return KEYS

def main():
    lists_of_student_grades:list[str,float] = list();
    last_2_min_grades:list[float] = []
    grades:list[float] = []
    for _ in range(int(input())):
        name = input()
        score = float(input())
        lists_of_student_grades.append([name,score])
    for i in range(len(lists_of_student_grades)):
        grades.append(lists_of_student_grades[i - 1][1])
    MIN = min(grades)
    for i in grades:
        if i != MIN:    last_2_min_grades.append(i)
        else:    continue
    if len(last_2_min_grades) == 1:    TARGET = last_2_min_grades[0]
    else:   TARGET = min(last_2_min_grades)

    KEY = get_key(lists_of_student_grades, TARGET)


    for i in sorted(KEY):
        print(i)

if __name__ == '__main__':
    main()