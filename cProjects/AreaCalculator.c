#include <stdio.h>


int main()
{
    double base, height;
    int shape;
    printf("Enter the Shape (1. Triangle, 2. Rectangle, 3. Circle): ");
    scanf("%d", &shape);

    while(shape < 1 || shape > 3)
    {
        printf("Invalid input. Please enter 1 for Triangle, 2 for Rectangle, or 3 for Circle: ");
        scanf("%d", &shape);
    }

    switch (shape)
    {
        case  1:
            printf("Enter the base and height of the triangle: ");
            scanf("%lf %lf", &base, &height);
            printf("Area of Triangle: %.2lf\n", 0.5 * base * height);
            break;
        case 2:
            printf("Enter the length and width of the rectangle: ");
            scanf("%lf %lf", &base, &height);
            printf("Area of Rectangle: %.2lf\n", base * height);
            break;
        case 3:
            printf("Enter the radius of the circle: ");
            scanf("%lf", &base);
            printf("Area of Circle: %.5lf\n", 3.141592653589793 * base * base);
            break;
        default:
            break;
    }
    return 0;
}