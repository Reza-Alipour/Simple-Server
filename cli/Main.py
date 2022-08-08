import cv2
from typing import List, Callable, Tuple, Dict

from cli.ClientData import ClientData

data: ClientData


def first_menu():
    run_menu([
        ('login', login_controller),
        ('register', lambda: print(data.post('/register',
                                             data={'username': input('Username: '), 'password': input('Password: '),
                                                   'role': input('role:(USER/ADMIN): ')}).text))
    ])


def user_menu():
    run_menu([
        ('see videos', see_videos),
        ('upload video',
         lambda: print(data.post('/video/upload', files={'file': open(input('File path: '), 'rb')}).text)),
        ('tickets', ticket_menu)
    ])


def admin_menu():
    run_menu([
        ('see users', see_users),
        ('see videos', see_videos),
        ('tickets', ticket_menu),
    ])


def see_users():
    role = data.role
    print(data.get('/admin/users').text)
    commands = []
    if role == 'ADMIN':
        commands.append(
            ('unstrike', lambda: print(data.post('/admin/unStrike', params={'userId': input('User id: ')}).text))
        )
    elif role == 'MANAGER':
        commands.append(
            ('approve admin',
             lambda: print(data.post('/manager/approveAdmin', params={'userId': input('User id: ')}).text))
        )
    run_menu(commands)


def manager_menu():
    run_menu([
        ('see users', see_users),
        ('see videos', see_videos),
        ('tickets', ticket_menu),
    ])


def see_videos(page_no: int = 0, page_size: int = 10):
    res = data.get('/video/getVideos', params={'pageNo': page_no, 'pageSize': page_size})
    if not res.ok:
        print('Error')
        return
    else:
        videos = res.json()
        for video in videos:
            print(
                f'{video["id"]} - {video["name"]} - likes: {video["likesNum"]} - dislikes: {video["dislikesNum"]} - path: {video["path"]}')

        command_tuples = [('see video', lambda: see_video(videos))]
        if len(videos) == page_size:
            command_tuples.append(('next page', lambda: see_videos(page_no + 1, page_size)))
        if page_no > 0:
            command_tuples.append(('previous page', lambda: see_videos(page_no - 1, page_size)))

        if data.role == 'ADMIN':
            command_tuples.append(
                (
                    'ban video',
                    lambda: print(data.post('/admin/banVideo', params={'videoId': input('Video id: ')}).text)))
            command_tuples.append(('add tag', lambda: print(
                data.post('/admin/addTag', params={'videoId': input('Video id: '), 'text': input('Tag: ')}).text)))

        run_menu(command_tuples)


def ticket_menu():
    commands = [('see tickets', see_tickets)]
    run_menu(commands)


def see_tickets():
    res = data.get('/ticket/getAll')
    if not res.ok:
        print('Error')
        return
    tickets = res.json()
    for ticket in tickets:
        print(ticket)
    run_menu(
        [('see ticket', lambda: print(data.get('/ticket/get', params={'id': int(input('Ticket id: '))}).text)),
         ('create ticket',
          lambda: print(data.post('/ticket/create', data={'message': input('Message: ')}).text)),
         ('add message', lambda: print(
             data.post('/ticket/addMessage', data={'id': input('Ticket id: '), 'message': input('Message: ')}).text)),
         ('change state',
          lambda: print(data.post('/ticket/changeState', data={'message': '', 'id': input('ticket id'), 'state': input(
              'Enter new state:(OPEN/CLOSED/IN_PROGRESS/RESOLVED)')}).text))])


def see_video(videos: List[Dict]):
    video_id = input('Video id: ')
    for video in videos:
        if str(video['id']) == video_id:
            video_menu(video['id'])
            break
    else:
        print('Video not found')


def video_menu(video_id):
    commands = [
        ('like', lambda: print(data.post('/user/like', params={'videoId': video_id}).text)),
        ('dislike', lambda: print(data.post('/user/dislike', params={'videoId': video_id}).text)),
        ('comment',
         lambda: print(data.post('/user/comment', data={'videoId': video_id, 'comment': input('Comment: ')}).text)),
        ('see comments', lambda: see_comments(video_id)),
        ('play', lambda: play(video_id)),
    ]
    if data.role == 'ADMIN':
        commands.append(('ban', lambda: print(data.post('/admin/banVideo', params={'videoId': video_id}).text)))
    run_menu(commands)


def see_comments(video_id):
    res = data.get('/user/comments', params={'videoId': video_id})
    if not res.ok:
        print('Error')
        return
    for comment in res.json():
        print(comment)


def play(videoId):
    path = data.get('/user/getPath', params={'videoId': videoId}).text
    url = 'http://localhost:8080/stream' + path
    print(url)
    cap = cv2.VideoCapture(url)
    cv2.startWindowThread()
    try:
        while True:
            ret, frame = cap.read()
            cv2.imshow('frame', frame)
            if cv2.waitKey(1) & 0xFF == ord('q'):
                cv2.destroyAllWindows()
                break
    except:
        cv2.destroyAllWindows()
        cv2.waitKey(1)


def login_controller():
    inputs = get_inputs(['username', 'password'])
    res = data.post('/login', inputs)
    if res:
        print('Login successful')
        data.role = res.text
        if res.text == 'USER':
            user_menu()
        elif res.text == 'ADMIN':
            admin_menu()
        elif res.text == 'MANAGER':
            manager_menu()
        elif res.text == 'ADMIN_PENDING':
            print('Your account is pending for manager approval')
        else:
            print('Some ??? error')
    else:
        print('Login failed')


def run_menu(commands: List[Tuple[str, Callable]]):
    print_commands(list(map(lambda x: x[0], commands)) + ['back', 'exit', 'ls'])
    while True:
        input_command = input('Command: ')
        if input_command == 'back':
            return
        elif input_command == 'exit':
            quit(0)
        elif input_command == 'ls':
            print_commands(list(map(lambda x: x[0], commands)) + ['back', 'exit', 'ls'])
            continue

        for command in commands:
            if command[0] == input_command:
                command[1]()
                break


def get_inputs(to_get: List[str]):
    inputs = {}
    for i in to_get:
        inputs[i] = input(f'{i}: ')
    return inputs


def print_commands(commands: List[str]):
    for i in range(len(commands)):
        print(f'{i} -  {commands[i]}')


if __name__ == '__main__':
    data = ClientData('http://localhost:8080')
    first_menu()
