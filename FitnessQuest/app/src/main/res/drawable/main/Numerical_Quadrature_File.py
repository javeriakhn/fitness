import numpy as np

def f(x):
    # This is your function to be integrated; adjust as needed
    return -12 * x ** 2 + 18 * x + np.sin(np.pi * x)

def Numerical_Quadrature(element_index, node_index, quad_type, h):
    """
    Adapts the Numerical_Quadrature function to the given parameters in the function call.

    Parameters:
        element_index (int): Index of the finite element (not used directly in calculation).
        node_index (int): Index of the node used to calculate xi and xj.
        quad_type (int): Specifies the quadrature rule type (1 for Trapezoidal, 2 for Simpson's).
        h (float): The step size or element size, used to calculate xi and xj.

    Returns:
        float: The result of the numerical integration.
    """
    # Calculate the starting and ending points of the integration interval
    xi = node_index * h
    xj = xi + h

    # Choose the quadrature rule based on quad_type
    if quad_type == 1:
        # Trapezoidal rule
        result = (xj - xi) * (f(xi) + f(xj)) / 2
    elif quad_type == 2:
        # Simpson's rule
        xm = (xi + xj) / 2
        result = (xj - xi) / 6 * (f(xi) + 4 * f(xm) + f(xj))
    else:
        raise ValueError("Invalid quadrature type specified")

    return result
