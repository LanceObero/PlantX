
from django.contrib.auth import authenticate
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.permissions import AllowAny
from rest_framework_simplejwt.tokens import RefreshToken

from .serializers import RegisterSerializer


class RegisterView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        serializer = RegisterSerializer(data=request.data)

        if serializer.is_valid():
            user = serializer.save()

            refresh = RefreshToken.for_user(user)

            return Response({
                "success": True,
                "message": "Registration successful.",
                "access": str(refresh.access_token),
                "refresh": str(refresh),
                "email": user.email,
                "full_name": user.get_full_name(),
            }, status=201)

        return Response(
            serializer.errors,
            status=400
        )


class LoginView(APIView):
    permission_classes = [AllowAny]

    def post(self, request):
        email = request.data.get("email")
        password = request.data.get("password")

        if not email or not password:
            return Response({
                "error": "Email and password are required."
            }, status=400)

        user = authenticate(
            request,
            username=email,
            password=password
        )

        if user is None:
            return Response({
                "error": "Invalid email or password."
            }, status=401)

        refresh = RefreshToken.for_user(user)

        return Response({
            "access": str(refresh.access_token),
            "refresh": str(refresh),
            "email": user.email,
            "full_name": user.get_full_name(),
        }, status=200)

