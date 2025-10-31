import sys
sys.stdout.reconfigure(encoding="utf-8")

import asyncio
import websockets
import json
import keyboard
import os
import pyautogui
import random
import time

from humanmouse import human_move

# Random sleep function
def sleep_random(lo, hi):
    time.sleep(random.uniform(lo / 1000, hi / 1000))

# Function to move the mouse and notify Java when done (async version)
async def move_mouse(ws, x, y):
    # Move the mouse with human-like randomness
    human_move(x, y)
    sleep_random(40, 60)  # Sleep to simulate a real delay

# ESC key listener function
def esc_listener():
    keyboard.wait("esc")
    print("ESC pressed. Shutting down.")
    os._exit(0)

# Handle incoming WebSocket messages
async def handle_connection(ws, path):
    print("→ client connected")
    try:
        async for raw in ws:
            print(f"← message from Java: {raw}")
            msg = json.loads(raw)
            msg_type = msg.get("type")

            # If the message type is "move_to", move the mouse and reply back
            if msg_type == "move_to":
                screen_x = msg.get("screenX")
                screen_y = msg.get("screenY")

                if screen_x is not None and screen_y is not None:
                    # Add some random offsets to simulate human-like movement
                    screen_x += random.randint(-3, 3)
                    screen_y += random.randint(-3, 3)
                    
                    print(f"✓ Moving to given location at {screen_x}, {screen_y}")

                    # Move the mouse and send a reply, all in the async function
                    await move_mouse(ws, screen_x, screen_y)
                    pyautogui.click()

    except websockets.exceptions.ConnectionClosed:
        print("← client disconnected")

# Main function to start the WebSocket server
async def main():
    # Start the ESC key listener in a separate thread
    import threading
    threading.Thread(target=esc_listener, daemon=True).start()

    # Start the WebSocket server and listen on ws://localhost:8080
    async with websockets.serve(handle_connection, "localhost", 8080):
        print("Python WS server listening on ws://localhost:8080")
        await asyncio.Future()  # Keep the server running

# Start the WebSocket server
if __name__ == "__main__":
    asyncio.run(main())
