import sys
import json
import os
import torch
from torchvision import transforms, models
from PIL import Image

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MODEL_DIR = os.path.join(BASE_DIR, "model")

MODEL_PATH = os.path.join(MODEL_DIR, "plant_model.pth")
CLASSES_PATH = os.path.join(MODEL_DIR, "classes.json")

device = torch.device("cpu")

with open(CLASSES_PATH, "r") as file:
    classes = json.load(file)

model = models.mobilenet_v3_small(weights=None)

model.classifier[3] = torch.nn.Linear(
    model.classifier[3].in_features,
    len(classes)
)

model.load_state_dict(
    torch.load(
        MODEL_PATH,
        map_location=device,
        weights_only=True
    )
)

model = model.to(device)
model.eval()

transform = transforms.Compose([
    transforms.Resize((224, 224)),
    transforms.ToTensor(),
    transforms.Normalize(
        [0.485, 0.456, 0.406],
        [0.229, 0.224, 0.225]
    )
])

if len(sys.argv) < 2:
    print("Please provide an image path.")
    print()
    print("Example:")
    print(
        r'.\venv\Scripts\python.exe plants\test_model.py "C:\Users\hp\Desktop\plant.jpg"'
    )
    sys.exit(1)

image_path = sys.argv[1]

if not os.path.exists(image_path):
    print("Image not found:")
    print(image_path)
    sys.exit(1)

try:
    image = Image.open(image_path).convert("RGB")
except Exception as e:
    print("Could not open image:", e)
    sys.exit(1)

image_tensor = transform(image).unsqueeze(0).to(device)

with torch.no_grad():
    output = model(image_tensor)
    probabilities = torch.softmax(output, dim=1)

confidence, predicted_index = torch.max(
    probabilities,
    dim=1
)

predicted_class = classes[predicted_index.item()]
confidence_percent = confidence.item() * 100

print()
print("PlantX AI Prediction")
print("---------------------")
print("Plant:", predicted_class)
print(f"Confidence: {confidence_percent:.2f}%")
print()

print("All predictions:")

for index, probability in enumerate(probabilities[0]):
    print(
        f"{classes[index]}: "
        f"{probability.item() * 100:.2f}%"
    )